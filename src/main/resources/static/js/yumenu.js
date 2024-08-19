// var addMenuBookmark = function () {
//     // 즐겨찾기 조회
//     $.getJSON('/api/system/bookmark', function (result) {
//         let _data = result.data;
//         var litagbook = '';
//
//         for (var i = 0; i < _data.length; i++) {
//             litagbook += '<li><a href="#" data-objid="' + _data[i].code + '" menuurl="' + _data[i].url + '">' + i18n.getMenuText(_data[i].name) + '</a></li>';
//         }
//
//         $('#bookmark-menu').html(litagbook);
//
//         // 이전에 바인딩된 클릭 이벤트 제거
//         $('#bookmark-menu a').off('click');
//
//         // 클릭 이벤트 바인딩
//         $('#bookmark-menu').on('click', 'a', function (e) {
//             e.preventDefault();
//             // 현재 클릭된 북마크에 class="on" 추가 및 다른 항목에서 제거
//             $('#bookmark-menu li').removeClass('on');
//             $(this).parent('li').addClass('on');
//
//             var val = $(this).text();
//             var menuurl = $(this).attr('menuurl');
//             var objid = $(this).attr('data-objid');
//
//
//             if (nthTabs.isExistsTab('#' + objid)) {
//                 nthTabs.toggleTab('#' + objid);
//
//             } else {
//                 nthTabs.addTab({
//                     id: objid,
//                     title: val,
//                     url: '/gui/' + objid + '/default',
//                     active: true,
//                     allowClose: true,
//                     isbookmark: _bookmark
//                 });
//             }
//         });
//
//     }).fail(function (e) {
//         Notify.error('즐겨찾기 메뉴 생성에 실패하였습니다.');
//     });
// }
