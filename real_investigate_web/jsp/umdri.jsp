<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  String s_sido = request.getParameter("sido");
  String s_umd = request.getParameter("umd");
  String s_Query = "";

  try{
    if("".equals(s_umd)) {
      s_Query = "select distinct UMD_CD CD, UMD_NM NM from real_investigate_config.tb_sidocode_2019 "+
                "where SIDOSGG_CD = '"+s_sido+"' order by UMD_CD";
    }
    else {
      s_Query = "select distinct RI_CD CD, RI_NM NM from real_investigate_config.tb_sidocode_2019 "+
                "where SIDOSGG_CD = '"+s_sido+"' and UMD_CD = '"+s_umd+"'";
    }

    pstmt = conn.prepareStatement(s_Query);
    rs = pstmt.executeQuery();
    while(rs.next()){
      out.println(rs.getString("CD")+","+rs.getString("NM")+",");
    }
  }catch(Exception e){
    out.print(e);
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
