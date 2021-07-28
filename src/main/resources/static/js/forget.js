$(function () {
    $(".feach-btn").click(feach);
});
function feach() {
    var btn = this;
    var count = 60;
    var email = $("#email").val();
    var reg = /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
    if (email == "") {
        alert("请输入邮箱!");
        return;
    }
    if(!reg.test(email)) {
        alert("邮箱格式不正确!");
        return;
    }

    const countDown = setInterval(function () {
        if (count===60) {
            $.post(
                CONTEXT_PATH + "/user/sendCode?p=" + Math.random(),
                {"email":$("#email").val()},
                function (data) {
                    data = $.parseJSON(data);
                    if (data.code != 0) {
                        alert(data.msg);
                    }
                }
            );
        }
        if (count===0) {
            $(btn).text('重新发送').removeAttr('disabled');
            $(btn).css({
                background: '#17a2b8',
                color: '#fff'
            });
            clearInterval(countDown);
        } else {
            $(btn).attr('disabled', true);
            $(btn).css({
                background: '#d8d8d8',
                color: '#707070'
            });
            $(btn).text(count + '秒后重新获取!');
        }
        count--;
    }, 1000);
}

