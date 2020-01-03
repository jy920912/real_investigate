<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  /*
   * 대상지 촬영 여부 업데이트, 삽입, 제거
   */
  String s_pnu    = request.getParameter("PNU");
  String s_capOnOff = request.getParameter("CAPONFF");
  String s_sndOnOff = request.getParameter("SNDONOFF");
  String s_drnOnOff = request.getParameter("DRNONOFF");
  String s_uid    = request.getParameter("UID");
  String s_sido = request.getParameter("SIDO");
  double d_coordx = Double.parseDouble(request.getParameter("X"));
  double d_coordy = Double.parseDouble(request.getParameter("Y"));
  try{
    //촬영 여부 업데이트
    if("U".equals(s_uid)){
      String s_Query = "update real_investigate_"+s_sido+".tb_pictureyesno set PICTURE_OX = ? "+
                       ",SEND_OX = ? ,DRON_OX = ? where PNU = ? ";
      pstmt = conn.prepareStatement(s_Query);
      pstmt.setString(1, s_capOnOff);
      pstmt.setString(2, s_sndOnOff);
      pstmt.setString(3, s_drnOnOff);
      pstmt.setString(4, s_pnu);
      int result = pstmt.executeUpdate();
      out.print(result);
    }
    //대상지 새로 추가
    else if("I".equals(s_uid)){
      String s_Query = "insert into real_investigate_"+s_sido+".tb_pictureyesno values(?,?,?,?,?,?)";
      pstmt = conn.prepareStatement(s_Query);
      pstmt.setString(1, s_pnu);
      pstmt.setString(2, s_capOnOff);
      pstmt.setString(3, s_sndOnOff);
      pstmt.setString(4, s_drnOnOff);
      pstmt.setDouble(5, d_coordx);
      pstmt.setDouble(6, d_coordy);
      int result = pstmt.executeUpdate();
      out.print(result);
    }
    //대상지 제거
    else if("D".equals(s_uid)){
      String s_Query = "delete from real_investigate_"+s_sido+".tb_pictureyesno where PNU = ?";
      pstmt = conn.prepareStatement(s_Query);
      pstmt.setString(1, s_pnu);
      int result = pstmt.executeUpdate();
      out.print(result);
    }
  }catch(Exception e){
    out.print("ERROR");
  }finally{
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
