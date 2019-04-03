$(function(){
    //close top evn alert begin
    $(".env-alert .close").click(function(){
        $(this).parent().hide(500, function(){
            $(".xl-content").css("top", $(".top-container").height() + "px");
        });
    });
    //close top evn alert end

    //change content top begin
    $(".xl-content").css("top", $(".top-container").height() + "px");
    //change content top end
})