<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  /*
   * 선택 대상지의 PNU, 지번 및 속성 데이터 select
   */
  String s_pnu = request.getParameter("name");
  String s_sido = request.getParameter("SIDO");
  int i_find = 0;
  try{
    String s_Query = "select PNU, NOW_ADDR, JIMOK, JIGA, AREA from "+
                     "real_investigate_"+s_sido+".tb_reference "+
                     "where PNU = ? ";
    pstmt = conn.prepareStatement(s_Query);
    pstmt.setString(1, s_pnu);
    rs = pstmt.executeQuery();
    while(rs.next()){
      out.print(rs.getString("NOW_ADDR")+"|"+rs.getString("PNU")+"|"+rs.getString("JIMOK")+"|"+
                  rs.getString("JIGA")+"|"+rs.getString("AREA")+"|");
    }
  }catch(Exception e){
    out.print("ERROR");
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
