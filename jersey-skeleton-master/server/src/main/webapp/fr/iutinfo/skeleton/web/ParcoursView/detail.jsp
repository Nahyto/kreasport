<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="fr">
  <head>
    <meta charset="utf-8">
    <title>Parcours : ${it.name}</title>
    <jsp:include page="/layout/head.jsp"/>
  </head>
  <body>
    <jsp:include page="/layout/navbar.jsp"/>
    <div class="container">
      <div class="row">
        <div class="col-md-6 col-md-offset-3">
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">Détail du "Parcours"</h3>
            </div>
            <div class="panel-body">
              Id : ${it.id}<br/>
              Nom : ${it.name}<br/>
              alias : ${it.alias} <br/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>