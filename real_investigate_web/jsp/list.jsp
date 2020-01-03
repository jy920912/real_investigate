<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  String s_sido = request.getParameter("sido");
  String s_switch = request.getParameter("switch");
  String s_umd = request.getParameter("umd");
  String s_ri = request.getParameter("ri");
  if("".equals(s_ri)) {
    s_ri = "00";
  }
  String s_Query = "";
  try{
    if("A".equals(s_switch)) {
      s_Query = "select RE.NOW_ADDR ADDR, YN.PICTURE_OX PICOX, YN.DRON_OX DRONOX from real_investigate_"+s_sido+".tb_pictureyesno YN "+
                       "inner join real_investigate_"+s_sido+".tb_reference RE on RE.pnu = YN.pnu "+
                       "where SUBSTR(YN.pnu,6,3) = '"+s_umd+"' and SUBSTR(YN.pnu,9,2) = '"+s_ri+"' order by RE.NOW_ADDR";
    }
    else if("O".equals(s_switch)) {
      s_Query = "select RE.NOW_ADDR ADDR, YN.PICTURE_OX PICOX, YN.DRON_OX DRONOX from real_investigate_"+s_sido+".tb_pictureyesno YN "+
                       "inner join real_investigate_"+s_sido+".tb_reference RE on RE.pnu = YN.pnu "+
                       "where SUBSTR(YN.pnu,6,3) = '"+s_umd+"' and SUBSTR(YN.pnu,9,2) = '"+s_ri+"' "+
                       "and (YN.PICTURE_OX = 'O' or YN.DRON_OX = 'O') order by RE.NOW_ADDR";
    }
    else if("X".equals(s_switch)) {
      s_Query = "select RE.NOW_ADDR ADDR, YN.PICTURE_OX PICOX, YN.DRON_OX DRONOX from real_investigate_"+s_sido+".tb_pictureyesno YN "+
                       "inner join real_investigate_"+s_sido+".tb_reference RE on RE.pnu = YN.pnu "+
                       "where SUBSTR(YN.pnu,6,3) = '"+s_umd+"' and SUBSTR(YN.pnu,9,2) = '"+s_ri+"' "+
                       "and YN.PICTURE_OX = 'X' and YN.DRON_OX = 'X' order by RE.NOW_ADDR";
    }
    else { out.println("ERROR"); return;}
    pstmt = conn.prepareStatement(s_Query);
    rs = pstmt.executeQuery();
    while(rs.next()){
      out.println(rs.getString("ADDR")+","+rs.getString("PICOX")+","+rs.getString("DRONOX")+",");
    }
  }catch(Exception e){
    out.print(e);
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
