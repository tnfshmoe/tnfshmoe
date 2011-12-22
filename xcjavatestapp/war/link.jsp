<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String link;

	link = request.getParameter("link");
%>

<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>Hello App Engine</title>
		
		<style type="text/css">
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
			.link_text{
				width:100%;;
				height:30px;
				font-size:20px;
				text-align:center;
			}
		</style>
		
		<script type="text/javascript" src="/highslide/highslide.js"></script>
		<link rel="stylesheet" type="text/css" href="/highslide/highslide.css" />
		<script type="text/javascript"src="/zeroclipboard/ZeroClipboard.js"></script>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/prototype/1.7.0.0/prototype.js"></script>
		<script type="text/javascript">
			var clip;
			
			function init(){
				ZeroClipboard.setMoviePath('/zeroclipboard/ZeroClipboard.swf');
				clip = new ZeroClipboard.Client();
				clip.setText('<%= link %>');
				clip.glue('button_copy');
				
				clip.addEventListener('mouseover',function(event){
					$('button_copy').className = 'select_button_hi';
				},false);
				clip.addEventListener('mouseout',function(event){
					$('button_copy').className = 'select_button';
				},false);
			}
		</script>
	</head>
	<body onload="init();">
		<div style="width:100%; margin:100px 0px; text-align:center;">
			<div style="width:300px; margin:0px auto;">
				<a href="<%= link %>" class="highslide" onclick="return hs.expand(this)">
					<img src="<%= link %>" height="300px" width="300px" />
				</a>
			</div>
			<input type="text" readonly="readonly" value="<%= link %>" class="link_text" style="width:400px; margin:20px 0px;" onclick="this.select();">
			<table style="margin:0px auto;">
				<tr>
					<td><div id="button_copy" class="select_button">複製</div></td>
					<td><div class="select_button" onclick="window.open('http://www.facebook.com/sharer/sharer.php?u=<%= link %>','_blank','toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no')" onmouseover="this.className = 'select_button_hi';" onmouseout="this.className = 'select_button';">Facebook</div></td>
					<td><div class="select_button_b" onclick="location.href = '/';" onmouseover="this.className = 'select_button_hi_b';" onmouseout="this.className = 'select_button_b';">上傳新圖片</div></td>
				</tr>
			</table>
		</div>
	</body>
</html>