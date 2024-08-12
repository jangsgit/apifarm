//

let MenuUtils = {
    addLog: function (gui_code) {

    },
};
// LBN 메뉴 처리 script (default layout 사용)
// 메뉴 세팅, 북마크, 클릭 이벤트 등

var addMenuBookmark = function () {

    // 즐겨찾기 조회

    $.getJSON('/api/system/bookmark', function (result) {
        let _data = result.data
        var litagbook = '';
        for (var i = 0; i < _data.length; i++) {
            litagbook += '<li><a href="#" data-manual="' + _data[i].ismanual + '" data-objid="' + _data[i].code + '" menuurl="' + _data[i].url + '">' + i18n.getMenuText(_data[i].name) + '</a></li>';
        }
        $('#bookmark-menu').html(litagbook);

        $('#bookmark-menu a').on('click', function (e) {
            e.preventDefault();
            // 현재 클릭된 북마크에 class="on" 추가 및 다른 항목에서 제거
            $('#bookmark-menu li').removeClass('on');
            $(this).parent('li').addClass('on');

            var val = $(this).text();
            var menuurl = $(this).attr('menuurl');
            var objid = $(this).attr('data-objid');
            var _manual = $(this).attr('data-manual');
            if (nthTabs.isExistsTab('#' + objid)) {
                nthTabs.toggleTab('#' + objid);
            } else {
                nthTabs.addTab({
                    id: objid,
                    title: val,
                    url: menuurl,
                    active: true,
                    allowClose: true,
                    ismanual: _manual
                });
                // menu_log insert 
            }
            // /* 탭 추가 생성시 북마크아이콘 추가*/
            // if (!$("[href*=#" + objid + "]").find("i").hasClass("fa-star")) {
            //     $("[href*=#" + objid + "]").prepend("<i class='fas fa-star bookmark'></i>");
            //     $(".nav-tabs .fa-star").off('click').on('click', function (e) {
            //         $(this).toggleClass("bookmark");
            //         fnBookMarkSave($(this).parent('a').prop('hash').replace('#', ''), $(this).hasClass('bookmark'));
            //     });
            // }
        });
    }).fail(function (e) {
        Notify.error('즐겨찾기 메뉴 생성에 실패하였습니다.');
    });
}

// 북마크 저장
var fnBookMarkSave = function (_objId, _isbookmark) {
    let csrf = $('[name=_csrf]').val();
    let param_data = {
        'menucode': _objId,
        isbookmark: _isbookmark,
        '_csrf': csrf
    };
    $.post('/api/system/bookmark/save', param_data, function (data) {
        if (_isbookmark) {
            $('.documents a[data-objid="' + _objId + '"]').attr('data-bookmark', 'true');
        } else {
            $('.documents a[data-objid="' + _objId + '"]').attr('data-bookmark', 'false');
        }
        addMenuBookmark();
    }).fail(function (e) {
        console.log('fnBookMarkSave error', e.message);
    });
}

// 대쉬보드에서 탭 북마크확인을 위한 호출함수
var fnCheckTabBookMark = function (objid, _bookmark) {
    if (!$("[href*=#" + objid + "]").find("i").hasClass("fa-star")) {
        $("[href*=#" + objid + "]").prepend("<i class='fas fa-star" + (_bookmark == 'true' ? ' bookmark' : '') + "'></i>");
        $(".nav-tabs .fa-star").off('click').on('click', function (e) {
            $(this).toggleClass("bookmark");
            fnBookMarkSave($(this).parent('a').prop('hash').replace('#', ''), $(this).hasClass('bookmark'));
        });
    }
}

var menuLink = function (objid, val, menuurl, manual) {
    if (menuurl != '#') {
        // 메뉴링크가 있을경우에만
        if (nthTabs.isExistsTab('#' + objid)) {
            nthTabs.toggleTab('#' + objid);
        } else {
            nthTabs.addTab({id: objid, title: val, url: menuurl, active: true, allowClose: true, ismanual: manual});
            // menu_log insert
        }
    }

    let $href = $("[href=#" + objid + "]");

    /* 탭 추가 생성시 북마크아이콘 추가*/
    if (!$href.find("i").hasClass("fa-star")) {
        if ($('#bookmark-menu a[data-objid=' + objid + ']').length == 1) {
            _bookmark = 'true';
        } else {
            _bookmark = 'false';
        }
        $href.prepend("<i class='fas fa-star" + (_bookmark == 'true' ? ' bookmark' : '') + "'></i>");

        $(".nav-tabs .fa-star").off('click').on('click', function (e) {
            $(this).toggleClass("bookmark");
            fnBookMarkSave($(this).parent('a').prop('hash').replace('#', ''), $(this).hasClass('bookmark'));
        });
    }
}

// 메뉴 조회
// function addMenu(menuData) {
//
//     for (var i = 0; i < menuData.length; i++) {
//         let node = menuData[i];
//
//
//         var strtxt = '';
//         for (var j = 0; j < node.nodes.length; j++) {
//             let subnode = node.nodes[j];
//             var sublitag = '<li>';
//             sublitag += '<a href="#" data-manual="' + subnode.ismanual
//                 + '" data-bookmark="' + subnode.isbookmark
//                 + '" data-objid="' + subnode.objId
//                 + '" menuurl="' + ((subnode.objUrl === '') ? '#' : subnode.objUrl) + '">'
//                 + subnode.objNm + '</a>';
//
//             sublitag += '<ul class="dep3"></ul>'; // 하위 메뉴를 위한 ul 추가
//             sublitag += '</li>';
//             strtxt += sublitag;
//         }
//
//     }
//
//     // 하위 메뉴 클릭 시 이벤트
//     $('.dep3').on('click', 'a', function (e) {
//         e.preventDefault();
//         var $li = $(this).parent('li');
//         if (!$li.hasClass('on')) {
//             $li.siblings('li').removeClass('on');
//             $li.addClass('on');
//         }
//
//         var val = $(this).text();
//         var menuurl = $(this).attr('menuurl');
//         var objid = $(this).attr('data-objid');
//         var _bookmark = $(this).attr('data-bookmark');
//         var _manual = $(this).attr('data-manual');
//
//         if (menuurl != '#') {
//             if (nthTabs.isExistsTab('#' + objid)) {
//                 nthTabs.toggleTab('#' + objid);
//             } else {
//                 nthTabs.addTab({
//                     id: objid,
//                     title: val,
//                     url: menuurl,
//                     active: true,
//                     allowClose: true,
//                     ismanual: _manual
//                 });
//             }
//         }
//     });
//
//
// }

$(document).ready(function () {

    // 메뉴 데이터를 API에서 가져와서 처리
    // $.getJSON('/api/system/menus', function (datas) {
    //     addMenu(datas.data);
    // }).fail(function (e) {
    //     console.error('Failed to load menu data.', e);
    // });

    // 즐겨찾기 메뉴 추가 함수
    function addMenuBookmark() {
        $.getJSON('/api/system/bookmark', function (result) {
            let bookmarks = result.data;
            let bookmarkHtml = '';

            bookmarks.forEach(bookmark => {
                bookmarkHtml += `<li>
                                    <a href="#" data-manual="${bookmark.ismanual}"
                                       data-objid="${bookmark.code}"
                                       menuurl="${bookmark.url}">
                                       ${bookmark.name}
                                    </a>
                                 </li>`;
            });

            $('#bookmark-menu').html(bookmarkHtml);

            $('#bookmark-menu').on('click', 'a', function (e) {
                e.preventDefault();
                // 현재 클릭된 북마크에 class="on" 추가 및 다른 항목에서 제거
                $('#bookmark-menu li').removeClass('on');
                $(this).parent('li').addClass('on');

                let val = $(this).text();
                let menuurl = $(this).attr('menuurl');
                let objid = $(this).attr('data-objid');
                let _manual = $(this).attr('data-manual');

                if (menuurl !== '#') {
                    if (nthTabs.isExistsTab('#' + objid)) {
                        nthTabs.toggleTab('#' + objid);
                    } else {
                        nthTabs.addTab({
                            id: objid,
                            title: val,
                            url: menuurl,
                            active: true,
                            allowClose: true,
                            ismanual: _manual
                        });
                    }
                }
            });
        }).fail(function (e) {
            console.error('Failed to load bookmarks.', e);
        });
    }

    // 즐겨찾기 메뉴 초기화
    addMenuBookmark();
});