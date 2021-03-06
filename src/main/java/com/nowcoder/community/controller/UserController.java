package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.*;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant{

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * ????????????
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        // ??????????????????
        String fileName = CommunityUtil.generateUUID();
        // ??????????????????
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // ??????????????????
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    /**
     * ??????????????????
     * @param fileName
     * @return
     */
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "?????????????????????!");
        }

        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);

        return CommunityUtil.getJSONString(0);
    }

    /**
     * ?????????
     * ??????????????????????????????
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "????????????????????????!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "????????????????????????!");
            return "/site/setting";
        }

        // ?????????????????????
        fileName = CommunityUtil.generateUUID() + suffix;
        // ???????????????????????????
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // ????????????
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("??????????????????: " + e.getMessage());
            throw new RuntimeException("??????????????????,?????????????????????!", e);
        }

        // ????????????????????????????????????(web????????????)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     * ?????????
     * ????????????????????????
     * @param fileName
     * @param response
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // ?????????????????????
        fileName = uploadPath + "/" + fileName;
        // ????????????
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // ????????????
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("??????????????????: " + e.getMessage());
        }
    }

    /**
     * ????????????
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("??????????????????!");
        }

        // ??????
        model.addAttribute("user", user);
        // ????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // ????????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // ????????????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // ???????????????
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

    /**
     * ????????????
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(path = "/my-post/{userId}", method = RequestMethod.GET)
    public String getMyDiscussPost(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("??????????????????!");
        }
        model.addAttribute("user", user);

        // ???????????????????????????
        page.setLimit(5);
        page.setPath("/user/my-post/" + userId);
        page.setRows(discussPostService.findDiscussPostRows(user.getId()));

        // ???????????????????????????
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(user.getId(), page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> list = new ArrayList<>();
        if (discussPosts != null) {
            for (DiscussPost post : discussPosts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                // ????????????
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                list.add(map);
            }
            model.addAttribute("discussPosts", list);
        }
        // ????????????
        int postCount = discussPostService.findDiscussPostRows(user.getId());
        model.addAttribute("postCount", postCount);

        return "/site/my-post";
    }

    /**
     * ????????????
     * @param userId
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/my-reply/{userId}", method = RequestMethod.GET)
    public String getMyReply(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("??????????????????!");
        }
        model.addAttribute("user", user);

        // ???????????????????????????
        page.setLimit(5);
        page.setPath("/user/my-reply/" + userId);
        page.setRows(commentService.findCommentCountById(user.getId()));

        // ????????????????????????????????????
        List<Comment> comments = commentService.findCommentsByUserId(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> list = new ArrayList<>();
        if (comments != null) {
            for (Comment comment : comments) {
                Map<String, Object> map = new HashMap<>();
                map.put("comment", comment);

                // ???????????????????????????
                String discussPostTitle = discussPostService.findDiscussPostById(comment.getEntityId()).getTitle();
                map.put("discussPostTitle", discussPostTitle);

                list.add(map);
            }
            model.addAttribute("comments", list);
        }

        // ???????????????
        int commentCount = commentService.findCommentCountById(user.getId());
        model.addAttribute("commentCount", commentCount);

        return "/site/my-reply";
    }

    /**
     * ????????????
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @param model
     * @return
     */
    @RequestMapping(path = "/updatePassword", method = {RequestMethod.GET, RequestMethod.POST})
    public String setPassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user, oldPassword, newPassword, confirmPassword);
        if (map == null || map.isEmpty()) {
            return "redirect:/index";
        }
        model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
        model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
        model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));
        return "/site/setting";
    }

    /**
     * ????????????
     * @return
     */
    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String forgetPassword() {
        return "/site/forget";
    }

    /**
     * ????????????????????????
     * @param email
     * @param response
     * @return
     */
    @RequestMapping(path = "/sendCode", method = RequestMethod.POST)
    @ResponseBody
    public String sendCode(String email, HttpServletResponse response) {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return CommunityUtil.getJSONString(1, "??????????????????????????????????????????!");
        }
        userService.sendCode(email, response);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * ?????????????????????????????????????????????????????????
     * @param email
     * @param code
     * @param password
     * @param model
     * @param codeOwner
     * @return
     */
    @RequestMapping(path = "/updatePasswordByCode", method = {RequestMethod.GET, RequestMethod.POST})
    public String updatePasswordByCode(String email, String code, String password, Model model, @CookieValue("codeOwner") String codeOwner) {
        System.out.println(code);
        String kaptcha = null;
        if (StringUtils.isNotBlank(codeOwner)) {
            String redisKay = RedisKeyUtil.getCodeKey(codeOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKay);
        } else {
            model.addAttribute("codeMsg", "???????????????!");
            return "/site/forget";
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equals(code)) {
            model.addAttribute("codeMsg", "??????????????????!");
            return "/site/forget";
        }

        Map<String, Object> map = userService.updatePasswordByCode(email, password);
        if (map.containsKey("success")) {
            return "redirect:/login";
        } else {
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }

    }
}
