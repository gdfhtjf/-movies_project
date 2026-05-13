<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理后台 — ${applicationScope.appName}</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Microsoft YaHei', sans-serif; background: #0d1117; color: #e6edf3; min-height: 100vh; }
        .header { background: #161b22; border-bottom: 3px solid #e63946; padding: 20px 32px; }
        .header-top { display: flex; justify-content: space-between; align-items: center; }
        .header h1 { color: #f4a261; font-size: 1.5rem; }
        .header .version { color: #8b949e; font-size: 0.75rem; background: #21262d; padding: 2px 10px; border-radius: 10px; }
        .header p { color: #8b949e; font-size: 0.82rem; margin-top: 6px; }
        .container { max-width: 1100px; margin: 32px auto; padding: 0 24px; }
        .stats { display: grid; grid-template-columns: repeat(5, 1fr); gap: 14px; margin-bottom: 28px; }
        .stat-card { background: linear-gradient(135deg, #1c2333, #161b22); border: 1px solid #30363d; border-radius: 12px; padding: 20px 16px; text-align: center; transition: transform 0.2s; }
        .stat-card:hover { transform: translateY(-3px); border-color: #f4a261; }
        .stat-card .num { font-size: 1.8rem; font-weight: 700; color: #f4a261; }
        .stat-card .label { color: #8b949e; font-size: 0.78rem; margin-top: 6px; }
        .section { background: #1c2333; border: 1px solid #30363d; border-radius: 12px; padding: 28px; margin-bottom: 24px; }
        .section h2 { color: #f4a261; font-size: 1.1rem; margin-bottom: 18px; padding-bottom: 12px; border-bottom: 1px solid #30363d; display: flex; align-items: center; gap: 8px; }
        .demo-row { display: flex; align-items: flex-start; padding: 12px 16px; background: #21262d; border-radius: 8px; margin-bottom: 10px; font-size: 0.85rem; border-left: 3px solid #30363d; transition: border-color 0.2s; }
        .demo-row:hover { border-left-color: #f4a261; }
        .demo-label { min-width: 180px; color: #8b949e; font-weight: 600; flex-shrink: 0; }
        .demo-code { color: #e63946; font-family: 'Consolas', 'Courier New', monospace; background: #0d1117; padding: 2px 8px; border-radius: 4px; font-size: 0.8rem; margin-right: 8px; display: inline-block; }
        .demo-result { color: #4CAF50; font-weight: 500; word-break: break-all; }
        .demo-warn { color: #FF9800; }
        .demo-error { color: #F44336; }
        .demo-info { color: #2196F3; }
        .tag-row { display: inline-flex; gap: 6px; flex-wrap: wrap; margin-left: 8px; }
        .badge { display: inline-block; padding: 2px 10px; border-radius: 10px; font-size: 0.72rem; font-weight: 700; }
        .badge-new { background: rgba(33,150,243,0.18); color: #2196F3; }
        .badge-hot { background: rgba(230,57,70,0.18); color: #e63946; }
        .badge-ok { background: rgba(76,175,80,0.18); color: #4CAF50; }
        .footer { text-align: center; padding: 24px; color: #8b949e; font-size: 0.78rem; border-top: 1px solid #30363d; margin-top: 32px; }
        nav { display: flex; gap: 16px; margin-bottom: 24px; }
        nav a { color: #8b949e; text-decoration: none; padding: 6px 16px; border-radius: 6px; background: #21262d; font-size: 0.85rem; transition: all 0.2s; }
        nav a:hover, nav a.active { color: #f4a261; background: #30363d; }
        .type-badge { display: inline-block; font-size: 0.7rem; padding: 1px 8px; border-radius: 8px; margin-left: 6px; background: rgba(244,162,97,0.15); color: #f4a261; }
    </style>
</head>
<body>
<div class="header">
    <div class="header-top">
        <h1>
            <c:out value="${applicationScope.appName}"/> — 管理后台
        </h1>
        <span class="version">${systemVersion}</span>
    </div>
    <p>
        【EL-1 作用域访问】启动时间: ${applicationScope.appStartTime}
        <span class="type-badge">scope</span>
    </p>
</div>

<div class="container">

    <nav>
        <a href="${pageContext.request.contextPath}/jsp/dashboard" class="active">仪表盘</a>
        <a href="${pageContext.request.contextPath}/jsp/movies">电影列表</a>
    </nav>

    <div class="stats">
        <div class="stat-card">
            <div class="num"><c:out value="${totalMovies}"/></div>
            <div class="label">电影总数</div>
        </div>
        <div class="stat-card">
            <div class="num"><c:out value="${totalUsers}"/></div>
            <div class="label">用户总数</div>
        </div>
        <div class="stat-card">
            <div class="num"><c:out value="${totalOrders}"/></div>
            <div class="label">订单总数</div>
        </div>
        <div class="stat-card">
            <div class="num"><c:out value="${totalScreenings}"/></div>
            <div class="label">场次总数</div>
        </div>
        <div class="stat-card">
            <div class="num">
                <c:choose>
                    <c:when test="${not empty onlineCount}">${onlineCount}</c:when>
                    <c:otherwise>0</c:otherwise>
                </c:choose>
            </div>
            <div class="label">当前在线</div>
        </div>
    </div>

    <div class="section">
        <h2>📌 EL 表达式语言演示 （9种类型）</h2>

        <div class="demo-row">
            <span class="demo-label">【EL-1】作用域访问</span>
            <span class="demo-code">\${applicationScope.appName}</span>
            <span>→</span>
            <span class="demo-result">${applicationScope.appName}</span>
            <span class="type-badge">scope</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【EL-2】empty 运算符</span>
            <span class="demo-code">\${empty totalMovies}</span>
            <span>→</span>
            <span class="demo-result">
                <c:choose>
                    <c:when test="${empty totalMovies}">true（列表为空）</c:when>
                    <c:otherwise>false（列表非空，共 ${totalMovies} 条）</c:otherwise>
                </c:choose>
            </span>
            <span class="type-badge">empty</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【EL-3】条件/三元运算符</span>
            <span class="demo-code">\${totalOrders > 0 ? '有订单' : '暂无订单'}</span>
            <span>→</span>
            <span class="demo-result">${totalOrders > 0 ? '有订单' : '暂无订单'}</span>
            <span class="type-badge">ternary</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【EL-4】算术运算</span>
            <span class="demo-code">\${totalMovies + totalScreenings}</span>
            <span>→</span>
            <span class="demo-result">资源总数 = ${totalMovies + totalScreenings}</span>
            <span class="type-badge">arithmetic</span>
        </div>

        <c:set var="demoA" value="25"/>
        <c:set var="demoB" value="10"/>

        <div class="demo-row">
            <span class="demo-label">【EL-5】关系运算（>=）</span>
            <span class="demo-code">\${demoA >= demoB}</span>
            <span>→</span>
            <span class="demo-result">${demoA} >= ${demoB} → ${demoA >= demoB}</span>
            <span class="type-badge">relational</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【EL-6】关系运算（!= / eq）</span>
            <span class="demo-code">\${demoA != demoB}</span>
            <span>→</span>
            <span class="demo-result">${demoA} != ${demoB} → ${demoA != demoB}</span>
            <span class="type-badge">relational</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【EL-7】逻辑运算</span>
            <span class="demo-code">\${demoA > 20 && demoB < 20}</span>
            <span>→</span>
            <span class="demo-result">(${demoA} > 20) && (${demoB} < 20) → ${demoA > 20 && demoB < 20}</span>
            <span class="type-badge">logical</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【EL-8】集合访问</span>
            <span class="demo-code">\${movies[0].title}</span>
            <span>→</span>
            <span class="demo-result">
                <c:choose>
                    <c:when test="${not empty movies}">${movies[0].title}</c:when>
                    <c:otherwise class="demo-warn">无数据</c:otherwise>
                </c:choose>
            </span>
            <span class="type-badge">collection</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【EL-9】Map 访问</span>
            <span class="demo-code">\${stats.avgPrice}</span>
            <span>→</span>
            <span class="demo-result">平均票价: ¥${stats.avgPrice}</span>
            <span class="type-badge">map</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【EL-10】request 参数</span>
            <span class="demo-code">\${param.sortBy}</span>
            <span>→</span>
            <span class="demo-result">
                <c:choose>
                    <c:when test="${not empty param.sortBy}">${param.sortBy}</c:when>
                    <c:otherwise class="demo-warn">（无 sortBy 参数，访问URL加 ?sortBy=price 试试）</c:otherwise>
                </c:choose>
            </span>
            <span class="type-badge">param</span>
        </div>
    </div>

    <div class="section">
        <h2>🏷 JSTL 标签演示 （11种类型）</h2>

        <div class="demo-row">
            <span class="demo-label">【标签-1】c:forEach 迭代</span>
            <span class="demo-code">&lt;c:forEach items="\${features}" var="f"&gt;</span>
            <span>→</span>
            <span class="demo-result tag-row">
                <c:forEach items="${features}" var="f" varStatus="vs">
                    <span class="badge badge-ok">${vs.count}.${f}</span>
                </c:forEach>
            </span>
            <span class="type-badge">iteration</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-2】c:choose/c:when 条件</span>
            <span class="demo-code">&lt;c:choose&gt;&lt;c:when&gt;</span>
            <span>→</span>
            <span class="demo-result">
                <c:choose>
                    <c:when test="${totalMovies > 10}">
                        <span class="badge badge-ok">运营良好</span> 影片充足（>10部）
                    </c:when>
                    <c:when test="${totalMovies > 0}">
                        <span class="badge badge-new">运营中</span> 建议补充影片
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-hot">暂无影片</span> 请尽快添加
                    </c:otherwise>
                </c:choose>
            </span>
            <span class="type-badge">condition</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-3】c:if 条件判断</span>
            <span class="demo-code">&lt;c:if test="\${totalOrders > 0}"&gt;</span>
            <span>→</span>
            <span class="demo-result">
                <c:if test="${totalOrders > 0}">
                    <span class="demo-info">✅ 已有 ${totalOrders} 笔交易</span>
                </c:if>
                <c:if test="${totalOrders == 0}">
                    <span class="demo-warn">⚠ 暂无订单</span>
                </c:if>
            </span>
            <span class="type-badge">condition</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-4】c:out 安全输出</span>
            <span class="demo-code">&lt;c:out value="&lt;script&gt;alert(1)&lt;/script&gt;" /&gt;</span>
            <span>→</span>
            <span class="demo-result">
                原文本: <c:out value="<script>alert(1)</script>" /> （已转义，防XSS）
            </span>
            <span class="type-badge">output</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-5】c:set 设置变量</span>
            <span class="demo-code">&lt;c:set var="greeting" value="欢迎光临" /&gt;</span>
            <span>→</span>
            <span class="demo-result">
                <c:set var="greeting" value="欢迎光临"/>
                <c:out value="${greeting}"/> （变量已设置并输出）
            </span>
            <span class="type-badge">variable</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-6】c:url 生成链接</span>
            <span class="demo-code">&lt;c:url value="/jsp/movies"&gt;</span>
            <span>→</span>
            <span class="demo-result">
                <c:set var="basePath" value="${pageContext.request.contextPath}"/>
                <c:url value="/jsp/movies" var="movieUrl">
                    <c:param name="sortBy" value="price"/>
                </c:url>
                <a href="${movieUrl}" style="color:#2196F3">${movieUrl}</a>
            </span>
            <span class="type-badge">url</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-7】fmt:formatDate 格式化</span>
            <span class="demo-code">&lt;fmt:formatDate value="\${now}" pattern="..." /&gt;</span>
            <span>→</span>
            <span class="demo-result">
                <jsp:useBean id="now" class="java.util.Date"/>
                <span class="demo-info"><fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                &nbsp;/&nbsp;
                <span class="demo-warn"><fmt:formatDate value="${now}" pattern="yyyy年MM月dd日"/></span>
            </span>
            <span class="type-badge">format</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-8】fmt:formatNumber 数字</span>
            <span class="demo-code">&lt;fmt:formatNumber value="1280000" type="currency" /&gt;</span>
            <span>→</span>
            <span class="demo-result">
                销售总额:
                <fmt:formatNumber value="${stats.totalSales}" type="currency" currencySymbol="¥"/>
                &nbsp;|&nbsp;
                平均分:
                <fmt:formatNumber value="${stats.ratingAvg}" maxFractionDigits="1"/>
            </span>
            <span class="type-badge">format</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-9】fn:length 长度</span>
            <span class="demo-code">\${fn:length(features)}</span>
            <span>→</span>
            <span class="demo-result">功能模块数量: ${fn:length(features)}</span>
            <span class="type-badge">function</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-10】fn:contains 包含</span>
            <span class="demo-code">\${fn:contains(str, '支付')}</span>
            <span>→</span>
            <span class="demo-result">
                <c:set var="testStr" value="在线支付系统"/>
                "${testStr}" 包含 "支付": ${fn:contains(testStr, '支付')}
            </span>
            <span class="type-badge">function</span>
        </div>

        <div class="demo-row">
            <span class="demo-label">【标签-11】fn:toUpperCase</span>
            <span class="demo-code">\${fn:toUpperCase('movie')}</span>
            <span>→</span>
            <span class="demo-result">'movie' 转大写: ${fn:toUpperCase('movie')}</span>
            <span class="type-badge">function</span>
        </div>
    </div>

    <div class="section">
        <h2>🎬 最近影片</h2>
        <c:choose>
            <c:when test="${not empty movies}">
                <c:forEach items="${movies}" var="m" varStatus="vs" begin="0" end="4">
                    <c:if test="${vs.count <= 5}">
                        <div class="demo-row">
                            <span class="demo-label">No.${vs.count}</span>
                            <span class="demo-code">${m.title}</span>
                            <span>导演: ${m.director}</span>
                            <span class="demo-result">¥${m.price}</span>
                            <span class="tag-row">
                                <c:choose>
                                    <c:when test="${m.price > 50}">
                                        <span class="badge badge-hot">热映</span>
                                    </c:when>
                                    <c:when test="${m.price > 20}">
                                        <span class="badge badge-new">推荐</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-ok">特价</span>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </c:if>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p style="color:#8b949e;text-align:center;padding:20px">
                    【EL-empty】暂无影片数据
                </p>
            </c:otherwise>
        </c:choose>
    </div>

</div>

<div class="footer">
    © ${applicationScope.appName} · ${systemVersion}
    | 监听器: ${fn:length(applicationScope.attributeNames) > 0 ? '已加载' : '未加载'}
    | <fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm"/>
</div>
</body>
</html>
