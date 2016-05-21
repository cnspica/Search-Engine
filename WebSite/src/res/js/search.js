var currentPage = 0;
var maxPageNumber = 10;
var linksPerPage = 10;
var linksLength = 0;
var links;

window.onload=function(){
    links = $.parseJSON(searchResults);
    linksLength = links.length;
    if(linksLength == 0){
        $("div.center").remove();
        informalError();
        return;
    }

    maxPageNumber = Math.floor(linksLength / linksPerPage);
    if (linksLength % linksPerPage > 0){
    maxPageNumber += 1;
    }

    $("#searchBox").attr("value",searchText);
    setCurrentPage(1);
}

function setCurrentPage(linkIndex){
    
    // handle prev and next page buttons 
    if(linkIndex == 0){
        if(currentPage == 1) return;
        pageNumber = currentPage - 1;
    }
    else if (linkIndex == 8){
        if(currentPage == maxPageNumber) return;
        pageNumber = currentPage * 1 + 1;
    }
    else{
        pageNumber = $("ul li a").get(linkIndex).innerHTML;
        if(pageNumber > maxPageNumber) return;
    }
    
    if(pageNumber == currentPage) return;
    
    // Manipulating the prev and next page buttons
    if(pageNumber == 1){
        $("#prevPage").addClass("disabled");
    }
    else{
        $("#prevPage").removeClass("disabled");
    }
    if(pageNumber == maxPageNumber){
        $("#nextPage").addClass("disabled");
    }
    else{
        $("#nextPage").removeClass("disabled");
    }
    
    var pages = $("ul li a");
    if(pageNumber > 4){
        for(var i = 1; i < 8;i++){
            var num = pageNumber - (4 - i);
            pages.get(i).innerHTML = num;
            if(num > maxPageNumber){
            $(pages.get(i)).addClass("disabled");
            }
            else{
            $(pages.get(i)).removeClass("disabled");
            }
        }
        setActive(4);
    }
    else{
        for(var i = 1; i < 8;i++){
            pages.get(i).innerHTML = i;
            if(i > maxPageNumber){
            $(pages.get(i)).addClass("disabled");
            }
            else{
            $(pages.get(i)).removeClass("disabled");
            }
        }
        setActive(pageNumber);
    }
    
    currentPage = pageNumber;
    generateResults();
}

function setActive(linkIndex){
    var pages = $("ul li a");
    $("ul li a").get(linkIndex).classList.add("active");
        for(var i = 1; i < 8;i++){
            if(i == linkIndex) continue;
            pages.get(i).classList.remove("active");
        }
}

function generateResults(){
    $("#links").empty();
    var start = linksPerPage*(currentPage-1);
    for (i = 0; i < linksPerPage && start + i < linksLength; i++) {
        $("#links").append(generateResult(start*1 + i));
    }
}

function generateResult(index){
    if(links[index].snippet === undefined){
        links[index].snippet = "This is Just a Snippet that is too long and have no idea what is that site or what it is about";
    }

    var $result = $(document.createElement('div'));
    $result.addClass("result");
    
    var $resultBody = $(document.createElement('div'));
    $resultBody.addClass("resultBody");
    
    var $resultTitle = $(document.createElement('h2'));
    $resultTitle.addClass("resultTitle");
    
    var $resultA = $(document.createElement('a'));
    $resultA.addClass("resultA");
    $resultA.html(links[index].title);
    $resultA.attr("href",links[index].url);
    
    var $resultSnippet = $(document.createElement('div'));
    $resultSnippet.addClass("resultSnippet");
    $resultSnippet.html(links[index].snippet);
    
    var $resultExtra = $(document.createElement('div'));
    $resultExtra.addClass("resultExtra");
    
    var $resultURL = $(document.createElement('a'));
    $resultURL.addClass("resultURL");
    $resultURL.html(links[index].url.substring(links[index].url.indexOf("//") + 2));
    $resultURL.attr("href",links[index].url);
    
    $result.append($resultBody);
    $resultBody.append($resultTitle);
    $resultTitle.append($resultA);
    $resultBody.append($resultSnippet);
    $resultBody.append($resultExtra);
    $resultExtra.append($resultURL);
    
    return $result
}

function formalError(){
    var $errorDiv = $(document.createElement('div'));
    $errorDiv.addClass("center");

    var $img = $(document.createElement('img'));
    $img.attr('src','/res/img/logo_header.v107.lg.svg');
    $img.attr('width','500px');
    $img.attr('height','500px');

    var $txtDiv = $(document.createElement('div'));
    $txtDiv.html("Sorry! We couldn't find any results!!");

    $errorDiv.append($img);
    $errorDiv.append($txtDiv);
    $($(".siteWrapper").get(0)).append($errorDiv);
}

function informalError(){
    // I have 6 different images for errors
    // I will choose the image randomly
    var randomImageIndex = Math.floor((Math.random() * 6) + 1);
    var imgPath = '/res/img/err' + randomImageIndex + '.jpg';
    var $errorDiv = $(document.createElement('div'));
    $errorDiv.addClass("center");

    var $img = $(document.createElement('img'));
    $img.attr('src',imgPath);
    $img.attr('width','auto');
    $img.attr('height','auto');

    $errorDiv.append($img);
    $($(".siteWrapper").get(0)).append($errorDiv);
}