<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${pageTitle} — ${applicationScope.appName}</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Microsoft YaHei',sans-serif; background: #0d1117; color: #e6edf3; }
        .header { background: #161b22; border-bottom: 2px solid #e63946; padding: 16px 32px; display: flex; justify-content: space-between; align-items: center; }
        .header h1 { color: #f4a261; }
        .header .meta { color: #8b949e; font-size: 0.82rem; }
        .container { max-width: 1100px; margin: 24px auto; padding: 0 24px; }
        nav { display: flex; gap: 16px; margin-bottom: 20px; }
        nav a { color: #8b949e; text-decoration: none; padding: 6px 16px; border-radius: 6px; background: #21262d; font-size: 0.85rem; transition: all 0.2s; }
        nav a:hover { color: #f4a261; background: #30363d; }
        .info-bar { background: #1c2333; border: 1px solid #30363d; border-radius: 10px; padding: 14px 20px; margin-bottom: 20px; font-size: 0.85rem; display: flex; gap: 32px; }
        .info-item { color: #8b949e; }
        .info-item strong { color: #f4a261; }
        table { width: 100%; border-collapse: collapse; background: #1c2333; border-radius: 10px; overflow: hidden; border: 1px solid #30363d; }
        th { background: #21262d; color: #f4a261; padding: 14px 16px; text-align: left; font-size: 0.85rem; }
        td { padding: 12px 16px; border-top: 1px solid #30363d; font-size: 0.84rem; }
        tr:hover td { background: #21262d; }
        .price { color: #e63946; font-weight: 700; }
        .tag { display: inline-block; padding: 2px 10px; border-radius: 4px; font-size: 0.72rem; font-weight: 600; }
        .tag-hot { background: rgba(230,57,70,0.15); color: #e63946; }
        .tag-cheap { background: rgba(76,175,80,0.15); color: #4CAF50; }
        .tag-new { background: rgba(33,150,243,0.15); color: #2196F3; }
        .footer { text-align: center; padding: 24px; color: #8b949e; font-size: 0.78rem; }
    </style>
</head>
<body>
<div class="header">
    <h1>🎬 ${pageTitle}</h1>
    <span class="meta">
        【EL-集合长度】共 ${fn:length(movies)} 部 |
        【EL-关系】最低 <strong style="color:#f4a261">¥${minPrice}</strong>
    </span>
</div>

<div class="container">

    <nav>
        <a href="${pageContext.request.contextPath}/jsp/dashboard">仪表盘</a>
        <a href="${pageContext.request.contextPath}/jsp/movies" style="color:#f4a261;background:#30363d">电影列表</a>
    </nav>

    <div class="info-bar">
        <div class="info-item">
            【标签-c:set】<c:set var="now" value="<%= new java.util.Date() %>"/>
            【标签-fmt:formatDate】<strong><fmt:formatDate value="${now}" pattern="yyyy-MM-dd"/></strong>
        </div>
        <div class="info-item">
            【标签-fmt:formatNumber】均价:
            <c:set var="avgPrice" value="${(minPrice + maxPrice) / 2}"/>
            <strong class="price"><fmt:formatNumber value="${avgPrice}" type="currency" currencySymbol="¥"/></strong>
        </div>
        <div class="info-item">
            【标签-fn:contains】<c:set var="domain" value="movie-system"/>
            环境: <strong style="color:#4CAF50">${fn:contains(domain, 'movie') ? '电影系统' : '未知'}</strong>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty movies}">
            <p style="text-align:center;padding:60px;color:#8b949e">
                【EL-empty】movies 为空，暂无影片数据 |
                【EL-logic】${empty movies && totalMovies == 0 ? '数据库无记录' : '数据异常'}
            </p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>#</th>
                    <th>电影名称</th>
                    <th>导演</th>
                    <th>类型</th>
                    <th>时长</th>
                    <th>价格</th>
                    <th>上映日期</th>
                    <th>评价</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${movies}" var="m" varStatus="vs">
                    <tr>
                        <td>${vs.count}</td>
                        <td>
                            <strong>${m.title}</strong>
                            <c:if test="${vs.first}">
                                <span class="tag tag-hot">最新</span>
                            </c:if>
                        </td>
                        <td>${m.director}</td>
                        <td>
                            ${m.genre}
                            <c:choose>
                                <c:when test="${fn:contains(m.genre, '科幻')}">
                                    <span class="tag tag-new">科幻</span>
                                </c:when>
                                <c:when test="${fn:contains(m.genre, '喜剧')}">
                                    <span class="tag tag-cheap">喜剧</span>
                                </c:when>
                            </c:choose>
                        </td>
                        <td>
                            【EL-算术】${m.duration} 分钟 = ${m.duration / 60} 小时
                        </td>
                        <td>
                            <span class="price">¥${m.price}</span>
                            <c:choose>
                                <c:when test="${m.price > 50}">
                                    <span class="tag tag-hot">热映</span>
                                </c:when>
                                <c:when test="${m.price > 20}">
                                    <span class="tag tag-new">推荐</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="tag tag-cheap">特价</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${not empty m.releaseDate}">
                                <fmt:formatDate value="${m.releaseDate}" pattern="yyyy-MM-dd"/>
                            </c:if>
                            <c:if test="${empty m.releaseDate}">
                                <span style="color:#8b949e">未定</span>
                            </c:if>
                        </td>
                        <td>
                            <c:set var="stars" value="★★★★★"/>
                            <c:set var="partial" value="${fn:substring(stars, 0, 4)}"/>
                            ${partial}☆
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

</div>

<div class="footer">
    © ${applicationScope.appName} · ${systemVersion}
    | 【标签-fn:toUpperCase】${fn:toUpperCase('movie ticket system')}
    | <fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm"/>
</div>
</body>
</html>
