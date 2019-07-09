


//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});

//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//验证手机号码
function checkPhone() {
	//获取用户输入的手机号码
	var phone = $.trim($("#phone").val());
	var flag = true;

	//手机号码不能为空
	if ("" == phone) {
		showError("phone","请输入手机号码");
		return false;
	} else if(!/^1[1-9]\d{9}$/.test(phone)) {
		//手机号码的格式
		showError("phone","请输入正确的手机号码");
		return false;
	} else {
		//验证手机号码是否重复
		$.ajax({
			url:"loan/checkPhone",
			type:"post",//往往向服务器获取数据用get，post:往往是向服务传递数据
			data:"phone="+phone,
			async:false,//关闭异步提交
			success:function (jsonObject) {
				if (jsonObject.errorMessage == "OK") {
					showSuccess("phone");
					flag = true;
				} else {
					showError("phone",jsonObject.errorMessage);
					flag = false;
				}
            },
			error:function () {
				showError("phone","系统繁忙，请稍后重试...");
				flag = false;
            }
		});
	}

	if (!flag) {
		return false;
	}

	return true;
}

//验证登录密码
function checkLoginPassword() {
	//获取用户输入的密码
	var loginPassword = $.trim($("#loginPassword").val());
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());

    /*密码验证格式：
	a)  密码不能为空
    b)  密码字符只可使用数字和大小写英文字母
    c)  密码应同时包含英文和数字
    d)  密码应为 6-16 位
    e)  两次输入密码必须一致*/

    if ("" == loginPassword) {
    	showError("loginPassword","请输入登录密码");
    	return false;
	} else if(!/^[0-9a-zA-Z]+$/.test(loginPassword)) {
    	showError("loginPassword","密码字符只可使用数字和大小写英文字母");
    	return false;
	} else if(!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
		showError("loginPassword","密码应同时包含英文和数字");
		return false;
	} else if(loginPassword.length < 6 || loginPassword.length > 16) {
    	showError("loginPassword","密码长度应为6-16位");
    	return false;
	} else {
    	showSuccess("loginPassword");
	}

	if (replayLoginPassword != loginPassword) {
		showError("replayLoginPassword","请再次输入登录密码");
	}

	return true;

}


//验证验证登录密码
function checkLoginPasswordEqu() {
	//获取用户输入的登录密码
	var loginPassword = $.trim($("#loginPassword").val());
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());

	if ("" == loginPassword) {
		showError("loginPassword","请输入登录密码");
		return false;
	} else if("" == replayLoginPassword) {
		showError("replayLoginPassword","请再次输入登录密码");
		return false;
	} else if(replayLoginPassword != loginPassword) {
		showError("replayLoginPassword","两次输入密码不一致");
		return false;
	} else {
		showSuccess("replayLoginPassword");
	}

	return true;

}


//验证图形验证码
function checkCaptcha() {
	//获取用户输入的图形验证码
	var captcha = $.trim($("#captcha").val());
	var flag = true;

	if ("" == captcha) {
		showError("captcha","请输入图形验证码");
		return false;
	} else {
		$.ajax({
			url:"loan/checkCaptcha",
			type:"get",
			data:"captcha="+captcha,
            async:false,//关闭异步提交
			success:function (jsonObject) {
				if (jsonObject.errorMessage == "OK") {
					showSuccess("captcha");
					flag = true;
				} else {
					showError("captcha",jsonObject.errorMessage);
					flag = false;
				}
            },
			error:function () {
				showError("captcha","系统繁忙，请稍后重试...");
				flag = false;
            }
		});
	}

	if (!flag) {
		return false;
	}

	return true;
}


//注册
function register() {
	//获取表单元素信息
	var phone = $.trim($("#phone").val());
	var loginPassword = $.trim($("#loginPassword").val());
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());

	if(checkPhone() && checkLoginPassword() && checkLoginPasswordEqu() && checkCaptcha()){

		$("#loginPassword").val($.md5(loginPassword));
		$("#replayLoginPassword").val($.md5(replayLoginPassword));
		
		$.ajax({
			url:"loan/register",
			type:"post",
			data:{
				"phone":phone,
				"loginPassword":$("#loginPassword").val(),
				"replayLoginPassword":$("#replayLoginPassword").val()
			},
			success:function (jsonObject) {
				if (jsonObject.errorMessage == "OK"){
					window.location.href = "realName.jsp";
				} else {
					showError("captcha",jsonObject.errorMessage);
				}
            },
			error:function () {
				showError("captcha","系统繁忙，请稍后重试...");

            }
		});

	}
}



















