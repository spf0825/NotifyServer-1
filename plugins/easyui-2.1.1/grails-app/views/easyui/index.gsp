<html>
    <head>
        <title>easyui Information</title>
        <meta name="layout" content="easyui" />
          <script type="text/javascript">
		$(function(){
			$('#note').propertygrid({
				width:550,
				height:'auto',
				url:'${request.contextPath }/easyui/notejson',
				showGroup:true,
				showHeader:true,
				scrollbarSize:0
			});
		});
		</script>
    </head>
    <body>
  <table id="note"></table>
    </body>
</html>
