function changeCode(){
    var node = document.getElementById("captcha_img");
    //修改验证码
    if (node){
        node.src = node.src+'?id='+uuid();
    }
}

function uuid(){
    var d = new Date().getTime();
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
    });
    return uuid;
};