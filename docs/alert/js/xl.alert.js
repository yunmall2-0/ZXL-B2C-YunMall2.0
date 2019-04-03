window.xlAlert = {
    openIconAlert: function(icon, title, btn, shadeClose, yesCallback, closeCallback){
        $(".xl-icon-alert img").attr("src", icon);
        layer.open({
            className: "xl-icon-alert"
            ,content: $(".xl-icon-alert").html()
            ,anim: false
            ,shade: true
            ,shadeClose: shadeClose
            ,style: ''
            ,btn: btn
            ,yes: function(){
                yesCallback();
            }
            ,no: function () {
                closeCallback();
            }
        });
    },
    openInputAlert: function(title, content, btn, yes, close){
        $(".xl-input-alert .title").text(title);
        $(".xl-input-alert .editor").attr("placeholder",content);
        $(".xl-input-alert .yes span").text(btn[0]);
        $(".xl-input-alert .close").text(btn[1]);
        layer.open({
            className: "xl-input-alert"
            ,content: $(".xl-input-alert").html()
            ,anim: false
            ,shade: true
            ,shadeClose: true
            ,style: 'background-color:rgba(255, 255, 255, 0); position: relative; top: 50px'
            ,success: function () {
                $(".xl-input-alert .yes span").unbind("click").bind("click", function(){
                    yes();
                    layer.closeAll();
                });
                $(".xl-input-alert .close").unbind("click").bind("click", function(){
                    close();
                    layer.closeAll();
                });
            }
        });
    }
}

