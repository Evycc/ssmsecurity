var scrollHeight = document.body.scrollTop;

window.onscroll = function(){
    scrollHeight =
        document.documentElement.scrollTop || document.body.scrollTop;
    var nav = document.getElementById('nav_head');
    if (scrollHeight >= 200) {
        nav.style.display = 'block';
    }else{
        nav.style.display = 'none';
    }
}
function liOnMouseOver(eleid){
    var li = document.getElementById(eleid);
    setTimeout(
        function(){
            var width = li.offsetWidth;
            console.log(width)
            if (width == 74) {
                if (eleid == 'liOne') {
                    li.innerText = "个人资料";
                }
                if (eleid == 'liTwo') {
                    li.innerText = "更改密码";
                }
                if (eleid == 'liThree') {
                    li.innerText = "退出登录";
                }
            }
        },1000);
}
function liOnMouseOut(eleid){
    var li = document.getElementById(eleid);
    li.innerText = ' ';
}
