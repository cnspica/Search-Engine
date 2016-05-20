<html lang="en_US">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>Duck .. Search Engine That Suck</title>
    <link rel="stylesheet" href="/res/css/search.css" type="text/css" />
    
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.3/jquery.min.js"></script>

    <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" sizes="16x16 24x24 32x32 64x64"/>
    <link rel="apple-touch-icon" href="/assets/icons/meta/DDG-iOS-icon_60x60.png"/>
    <link rel="apple-touch-icon" sizes="76x76" href="/assets/icons/meta/DDG-iOS-icon_76x76.png"/>
    <link rel="apple-touch-icon" sizes="120x120" href="/assets/icons/meta/DDG-iOS-icon_120x120.png"/>
    <link rel="apple-touch-icon" sizes="152x152" href="/assets/icons/meta/DDG-iOS-icon_152x152.png"/>
    <link rel="image_src" href="/assets/icons/meta/DDG-icon_256x256.png"/>
    <script
     type='text/javascript'> var searchResults = '<%= request.getAttribute("searchResults")%>';
     var searchText = '<%= request.getAttribute("searchText")%>';
    </script>
</head>
<body>
    <div class="siteWrapper">
        <div class="headerWrapper">
            <div class="header">
                <div class="headerSearch">
                    <a href="/" class="logoWrapper"><span class="logo"></span></a>
                    <div class="headerSearchForumWrapper">
                        <form class="searchForum" action="search" method="get" name="x">
                            <input id="searchBox" type="text" name="q" tabindex="1" autocomplete="off" value="">
                            <input type="button" tabindex="3" value="X" class="searchButton">
                            <input type="submit" tabindex="2" value="S" class="searchSubmit">
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="contentWrapper">
            <div class="cw">
                <div class="linksWrapper">
                    <div id="links" class="results">
                    </div>
                </div>
            </div>
        </div>
        <div class="center">
            <ul class="pagination">
                <li><a id="prevPage" onclick="setCurrentPage('0')"><</a></li>
                <li><a onclick="setCurrentPage('1')">1</a></li>
                <li><a onclick="setCurrentPage('2')">2</a></li>
                <li><a onclick="setCurrentPage('3')">3</a></li>
                <li><a onclick="setCurrentPage('4')">4</a></li>
                <li><a onclick="setCurrentPage('5')">5</a></li>
                <li><a onclick="setCurrentPage('6')">6</a></li>
                <li><a onclick="setCurrentPage('7')">7</a></li>
                <li><a id="nextPage" onclick="setCurrentPage('8')">></a></li>
            </ul>
        </div>
    </div>
    <script type="text/javascript" src="/res/js/search.js"></script>
</body>
</html>
