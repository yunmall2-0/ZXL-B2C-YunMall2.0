$(function(){
    $(".bank-list li").each(function(index,obj){
        var bank = $(obj).data("bank");
        $(obj).find(".img").css("background-image", "url('" + "//apimg.alipay.com/combo.png?d=cashier&t=" + bank + "')");
    })
})