<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.blobstore.*" %>

<%
	BlobstoreService bs;

	bs = BlobstoreServiceFactory.getBlobstoreService();
%>

<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>Hello App Engine</title>
		
		<style type="text/css">	
			.div_fullscreen{
				width:100%;
				height:100%;
				position:absolute;
				top:0px; left:0px;
			}
			
			.select_button{
				width:100px;
				height:50px;
				margin:0px auto;
				color:#FAFAFA;
				font-weight:bold;
				background-color:#F62217;
				border:1px solid #E41B17;
				text-align:center;
				line-height:50px;
				cursor:default;
				overflow:hidden;
			}	
			.select_button_hi{
				width:100px;
				height:50px;
				margin:0px auto;
				color:#FAFAFA;
				font-weight:bold;
				background-color:#FF3025;
				border:1px solid #E41B17;
				text-align:center;
				line-height:50px;
				cursor:default;
				overflow:hidden;
			}
			.select_button_b{
				width:100px;
				height:50px;
				margin:0px auto;
				color:#FAFAFA;
				font-weight:bold;
				background-color:#306EFF;
				border:1px solid #2B65EC;
				text-align:center;
				line-height:50px;
				cursor:default;
				overflow:hidden;
			}	
			.select_button_hi_b{
				width:100px;
				height:50px;
				margin:0px auto;
				color:#FAFAFA;
				font-weight:bold;
				background-color:#3C77FF;
				border:1px solid #2B65EC;
				text-align:center;
				line-height:50px;
				cursor:default;
				overflow:hidden;
			}
			.post_file{
				width:0px;
				font-size:300px;
				position:relative;
				left:-100px;
				top:-100px;
				opacity:0;
				filter:alpha(opacity=0);	
				z-index:1000;
			}	
			.post_text{
				width:100%;;
				height:30px;
				font-size:20px;
				text-align:center;
			}
		</style>
		
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/prototype/1.7.0.0/prototype.js"></script>
		<script type="text/javascript">
			var specFlag;
			var postType;
			var currFile;
		
			function init(){
				var e_div_main;
				
				if(typeof(FormData) == 'undefined'){
					specFlag = false;
				}else{
					specFlag = true;
				}
				
				if(specFlag == true){
					e_div_main = $('div_main');
					
					e_div_main.observe('dragenter',function(event){event.stop();});
					e_div_main.observe('dragexit',function(event){event.stop();});
					e_div_main.observe('dragover',function(event){event.stop();});
					e_div_main.observe('drop',function(event){
						var e_input_post_text;
	
						postType = 'file';
						
						currFile = event.dataTransfer.files[0];
						
						e_input_post_text = document.getElementsByName('input_post_text')[0];
						e_input_post_text.readOnly = true;
						e_input_post_text.value = currFile.name;
						
						$('div_data').style.display = '';
						$('button_submit').style.display = '';
						
						event.stop();
					});
				}
			}
			function postfile(){
				var e_input_post_file;
				var e_input_post_text;
				var fileName;

				postType = 'file';
				
				e_input_post_file = document.getElementsByName('input_post_file')[0];
				
				if(specFlag == true){
					currFile = e_input_post_file.files[0];
				}
				
				fileName = e_input_post_file.value.split(/[\/\\]/);
				fileName = fileName[fileName.length - 1];
				e_input_post_text = document.getElementsByName('input_post_text')[0];
				e_input_post_text.readOnly = true;
				e_input_post_text.value = fileName;
				
				$('div_data').style.display = '';
				$('button_submit').style.display = '';
			}
			function posturl(){
				var e_input_post_text;
				
				postType = 'url';
				
				e_input_post_text = document.getElementsByName('input_post_text')[0];
				e_input_post_text.readOnly = false;
				e_input_post_text.value = '';
				
				$('div_data').style.display = '';
				$('button_submit').style.display = '';
			}
			function submitfile(){
				var e_input_post_type;
				var e_input_post_file;
				var e_form;
				
				$('div_mask').style.display = '';
				$('div_mask_upload').style.display = '';
				
				e_input_post_type = document.getElementsByName('input_post_type')[0];
				e_input_post_type.value = postType;
				
				if(specFlag == true){
					e_form = new FormData();
					
					e_form.append('input_post_type',e_input_post_type.value);
					e_form.append('input_post_delpw',document.getElementsByName('input_post_delpw')[0].value);
					if(postType == 'file'){
						e_form.append('input_post_text',document.getElementsByName('input_post_text')[0].value);
						e_form.append('input_post_file',currFile);
					}else if(postType == 'url'){
						e_form.append('input_post_text',document.getElementsByName('input_post_text')[0].value);	
					}
					
					var xhr = new XMLHttpRequest();
					xhr.onreadystatechange = function(){
						if(xhr.readyState == 4 && xhr.status == 200){
							location.href = xhr.responseText;
						}
					}
					xhr.open('POST','<%= bs.createUploadUrl("/upload") %>', true);
					xhr.send(e_form);
				}else{
					e_input_post_file = document.getElementsByName('input_post_file')[0];
					if(postType == 'url'){
						e_input_post_file.disabled = 'disabled';
					}
					
					$('form_post').submit();	
				}
			}
		</script>
	</head>
	<body style="height:100%; margin:0; padding:0;" onload="init();">
		<div id="div_mask" class="div_fullscreen" style="background-color:#000000; background-color:rgba(0,0,0,0.8); text-align:center; z-index:10000; display:none;">
			<div id="div_mask_upload" style="margin:300px; auto; color:#FFFFFF; font-size:60px; font-weight:bold; display:none;">上傳中...</div>
		</div>
		<div id="div_main" class="div_fullscreen" style="text-align:center; z-index:0;">
			<table>
				<tr>
					<td><a href="/index.jsp" style="font-weight:bold;">上傳</a></td>
					<td><a href="/gallery.jsp" style="font-weight:bold;">畫廊</a></td>
				</tr>
			</table>
			<div style="width:400px; margin:200px auto; text-align:center;">
				<form id="form_post" action="/upload" method="post" enctype="multipart/form-data">
					<input name="input_post_type" type="hidden">
					<div id="div_data" style="display:none; text-align:center;">
						<input name="input_post_text" type="text" class="post_text">
						<table style="width:100%; margin:10px 0px; text-align:center; border:#D9D9D9 1px solid; border-collapse:collapse;">
							<tr>
								<td style="width:30%; font-weight:bold; border:#D9D9D9 1px solid;">刪除密碼</td>
								<td style="border:#D9D9D9 1px solid;"><input name="input_post_delpw" type="text" style="width:100%; height:20px;"></td>
							</tr>
						</table>
					</div>
					<table style="margin:20px auto;">
						<tr>
							<td>
								<div class="select_button" onmouseover="this.className = 'select_button_hi';" onmouseout="this.className = 'select_button';">
									<div style="width:100px; position:absolute;">選擇檔案</div>
									<input name="input_post_file" type="file" class="post_file" onchange="postfile();">
								</div>
							</td>
							<td><div class="select_button" onclick="posturl();" onmouseover="this.className = 'select_button_hi';" onmouseout="this.className = 'select_button';">圖片網址</div></td>
							<td><div id="button_submit" class="select_button_b" style="display:none;" onclick="submitfile();" onmouseover="this.className = 'select_button_hi_b';" onmouseout="this.className = 'select_button_b';">上傳</div></td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</body>
</html>