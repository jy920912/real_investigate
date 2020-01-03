<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  /*
   * 대상지 목록 패널 중 읍면동리 스피너에 들어갈 읍면동 및 리 select
   */
  String s_sido = request.getParameter("sido");
  String s_umd = request.getParameter("umd");
  String s_Query = "";

  try{
    //읍면동
    if("".equals(s_umd)) {
      s_Query = "select distinct UMD_CD CD, UMD_NM NM from real_investigate_config.tb_sidocode_2019 "+
                "where SIDOSGG_CD = '"+s_sido+"' order by UMD_CD";
    }
    //리
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
    out.print("ERROR");
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
