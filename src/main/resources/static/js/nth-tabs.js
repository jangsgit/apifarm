/**
 * nth-tabs
 * author:nethuige
 * version:2.0
 */
(function ($) {
    $.fn.nthTabs = function (options) {
        var nthTabs = this;
        var delflag = false;

        var defaults = {
            allowClose: true,
            active: true,
            location: true,
            fadeIn: true,
            rollWidth: nthTabs.width() - 120
        };

        var settings = $.extend({}, defaults, options);

        var handler = [];
        var frameName = 0;

        var template =
            '<div class="page-tabs">' +
            '<a href="#" class="roll-nav roll-nav-left"><span class="fa fa-angle-left"></span><span class="hidden">TAB move left</span></a>' +
            '<div class="content-tabs">' +
            '<div class="content-tabs-container">' +
            '<ul class="nav nav-tabs" id="tabdragdrop"></ul>' +
            '</div>' +
            '</div>' +
            '<a href="#" class="roll-nav roll-nav-right"><span class="fa fa-angle-right"></span><span class="hidden">TAB move right</span></a>' +
            '<ul id="contextTabMenu" class="dropdown-menu tabmenus animated flipInX">' +
            '<li><a href="#" class="tab-open-current">새창으로 열기</a></li>' +
            '<li><a href="#" class="tab-close-current otherAuthmaintab">현재 탭 닫기</a></li>' +
            '<li role="separator" class="divider"></li>' +
            '<li><a href="#" class="tab-close-other otherAuthtab otherAuthmaintab">다른 탭 닫기</a></li>' +
            '<li><a href="#" class="tab-close-all">모든 탭 닫기</a></li>' +
            '</ul>' +
            '</div>' +
            '<div class="tab-content"></div>';

        var run = function () {
            nthTabs.html(template);
            event.onWindowsResize().onTabClose().onTabRollLeft().onTabRollRight().onTabList()
                .onTabCloseOpt().onTabNewWindow().onTabCloseAll().onTabCloseOther().onLocationTab().onTabToggle();
            return methods;
        };

        var methods = {
            getAllTabWidth: function () {
                var sum_width = 0;
                nthTabs.find('.nav-tabs li').each(function () {
                    sum_width += parseFloat($(this).width());
                });
                return sum_width;
            },

            getMarginStep: function () {
                return settings.rollWidth / 2;
            },

            getActiveId: function () {
                return nthTabs.find('li[class="active"]').find("a").attr("href").replace('#', '');
            },

            getTabList: function () {
                var tabList = [];
                nthTabs.find('.nav-tabs li a').each(function () {
                    tabList.push({ id: $(this).attr('href'), title: $(this).children('span').html() });
                });
                return tabList;
            },

            addTab: function (options) {
                var tab = [];
                var active = options.active == undefined ? settings.active : options.active;
                var allowClose = options.allowClose == undefined ? settings.allowClose : options.allowClose;
                var location = options.location == undefined ? settings.location : options.location;
                var fadeIn = options.fadeIn == undefined ? settings.fadeIn : options.fadeIn;
                var url = options.url == undefined ? "" : options.url;
                var ismanual = options.ismanual == undefined ? 'false' : options.ismanual;
                var isbookmark = options.isbookmark == undefined ? 'false' : options.isbookmark;
                var objid = options.id;

                // tab.push('<li data-title="' + options.title + '" ' + (allowClose ? '' : 'not-allow-close') + '>');
                tab.push('<li data-title="' + options.title + '" data-isbookmark="' + isbookmark + '"' + (allowClose ? '' : 'not-allow-close') + '>');
                tab.push('<a href="#' + options.id + '" data-toggle="tab" data-optionurl="' + options.url + '">');
                tab.push('<span>' + options.title + '</span>');
                ismanual == 'true' ? tab.push('<i class="fas fa-question-circle tab-question" data-objid="' + options.id + '" title="매뉴얼"></i>') : '';
                tab.push('</a>');
                allowClose ? tab.push('<i class="icon nth-icon-close-mini tab-close"></i>') : '';
                tab.push('</li>');
                nthTabs.find(".nav-tabs").append(tab.join(''));

                var tabContent = [];
                tabContent.push('<div class="tab-pane ' + (fadeIn ? 'animation-fade' : '') + '" id="' + options.id + '" ' + (allowClose ? '' : 'not-allow-close') + '>');
                if (url.length > 0) {
                    tabContent.push('<iframe src="' + options.url + '" frameborder="0" name="iframe-' + frameName + '" class="nth-tabs-frame"></iframe>');
                    frameName++;
                } else {
                    tabContent.push('<div class="nth-tabs-content">' + options.content + "</div>");
                }
                tabContent.push('</div>');
                nthTabs.find(".tab-content").append(tabContent.join(''));
                active && this.setActTab(options.id);
                location && this.locationTab(options.id);

                // 탭 로드 후 북마크 이벤트 바인딩
                this.bindBookmarkEvent(objid, isbookmark);

                sortable('#tabdragdrop', {
                    forcePlaceholderSize: true
                });

                $('.tab-question').bind('click', function (e) {
                    Ax5Modal.open({ url: '/modal/manual', width: 800, height: 600, callbackfn: 'setPopUpManualResult', params: { objId: $(this).data('objid') } });
                });
                return this;
            },

            addTabs: function (tabsOptions) {
                for (var index in tabsOptions) {
                    this.addTab(tabsOptions[index]);
                }
                return this;
            },

            bindBookmarkEvent: function(objid, isbookmark) {
                var iframe = document.querySelector('#' + objid + ' iframe');
                if (iframe) {
                    iframe.onload = function() {
                        var bookmarkButton = iframe.contentWindow.document.querySelector('.bookmark.toggle');
                        if (bookmarkButton) {
                            // 북마크 초기 상태 설정
                            if (isbookmark === 'true') {
                                bookmarkButton.classList.add('on');
                            } else {
                                bookmarkButton.classList.remove('on');
                            }

                            // 북마크 클릭 이벤트 바인딩
                            bookmarkButton.addEventListener('click', function() {

                                var menuCode = objid;
                                var isBookmarked = bookmarkButton.classList.contains('on');
                                let csrf = document.querySelector('[name=_csrf]').value;

                                // 현재 탭의 제목을 가져옴
                                var currentTabTitle = $('#main-tabs').find('a[href="#' + objid + '"] span').text();
                                var menuUrl = bookmarkButton.getAttribute('menuurl');  // URL도 가져오기

                                // 북마크 상태 저장
                                $.ajax({
                                    url: '/api/system/bookmark/save',
                                    type: 'POST',
                                    data: {
                                        menucode: menuCode,
                                        isbookmark: isBookmarked ? 'true' : 'false', // 북마크 상태를 반전시켜 전송
                                        '_csrf': csrf
                                    },
                                    success: function (response) {
                                        if (response.success) {
                                            if (!isBookmarked) { // 북마크를 추가하는 경우

                                                // 탭의 data-isbookmark 속성 업데이트
                                                $('a[href="#' + objid + '"]').closest('li').attr('data-isbookmark', 'true');

                                                // 북마크 메뉴에 항목 추가
                                                if ($('#bookmark-menu a[data-objid="' + menuCode + '"]').length === 0) {
                                                    $('#bookmark-menu').append('<li><a  data-objid="' + menuCode + '" menuurl="' + menuUrl + '">' + currentTabTitle + '</a></li>');
                                                }

                                            } else { // 북마크를 제거하는 경우

                                                // 탭의 data-isbookmark 속성 업데이트
                                                $('a[href="#' + objid + '"]').closest('li').attr('data-isbookmark', 'false');

                                                // 북마크 메뉴에서 항목 제거
                                                $('#bookmark-menu a[data-objid="' + menuCode + '"]').parent('li').remove();
                                            }
                                            // 메인 창에 북마크가 변경되었음을 알림
                                            if (window.opener) {
                                                var event = new CustomEvent('bookmarkChanged');
                                                window.opener.dispatchEvent(event);
                                            }
                                        } else {
                                            console.error('Failed to save bookmark.');
                                        }
                                    },
                                    error: function () {
                                        console.error('Error occurred while saving bookmark.');
                                    }
                                });
                            });
                        }
                    };
                }
            },

            locationTab: function (tabId) {
                tabId = tabId == undefined ? methods.getActiveId() : tabId;
                tabId = tabId.indexOf('#') > -1 ? tabId : '#' + tabId;
                var navTabOpt = nthTabs.find("[href='" + tabId + "']");

                var beforeTabsWidth = 0;
                navTabOpt.parent().prevAll().each(function () {
                    beforeTabsWidth += $(this).width();
                });

                var contentTab = navTabOpt.parent().parent().parent();

                var margin_left_total = 40;
                if (beforeTabsWidth > settings.rollWidth) {
                    margin_left_total = 40 - Math.floor(beforeTabsWidth / settings.rollWidth) * settings.rollWidth;
                }

                contentTab.css("margin-left", margin_left_total);
                return this;
            },

            delTab: function (tabId) {
                tabId = tabId == undefined ? methods.getActiveId() : tabId;
                tabId = tabId.indexOf('#') > -1 ? tabId : '#' + tabId;
                var navTabA = nthTabs.find("[href='" + tabId + "']");
                if (navTabA.parent().attr('not-allow-close') != undefined) return false;

                if (navTabA.parent().attr('class') == 'active') {
                    var activeNavTab = navTabA.parent().next();
                    var activeTabContent = $(tabId).next();
                    if (activeNavTab.length < 1) {
                        activeNavTab = navTabA.parent().prev();
                        activeTabContent = $(tabId).prev();
                    }
                    activeNavTab.addClass('active');
                    activeTabContent.addClass('active');
                }

                navTabA.parent().remove();
                $(tabId).remove();

                var $delTabObj = $('#menu').find('a[data-objid="' + tabId.replace('#', '') + '"]').closest('li.has_sub');
                $delTabObj.removeClass('open');
                $delTabObj.children('ul').hide();

                delflag = true;
                return this;
            },

            openNewWindow: function (_targetObjId) {
                var navTabA = nthTabs.find("[href='" + _targetObjId + "']");
                this.openWindowTab(navTabA.data('optionurl'), {
                    width: 1280,
                    height: 720,
                    winname: 'newTabWindow-' + _targetObjId,
                    hideBookmark: true,  // 플래그 추가
                    params: { wintitle: encodeURI(navTabA.closest('li').data('title'))
                               }
                });
                return this;
            },

            openWindowTab: function (url, options) {
                if (!options) {
                    options = {};
                }

                if (!options.width) {
                    options.width = 1024;
                }
                if (!options.height) {
                    options.height = 768;
                }
                if (!options.layout) {
                    options.layout = 'resizable=no, toolbar=no, menubar=no, location=no, status=no, scrollbars=yes';
                }
                if (!options.winname) {
                    options.winname = '__window__' + Math.floor((Math.random() * 1000000) + 1);
                }

                var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
                var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;
                var screenWidth = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
                var screenHeight = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
                if (!options.left) {
                    options.left = (screenWidth / 2) - (options.width / 2) + dualScreenLeft;
                }
                if (!options.top) {
                    options.top = (screenHeight / 2) - (options.height / 2);
                }

                // 여기에서 hideBookmark를 URL에 추가
                if (options.hideBookmark) {
                    url += (url.indexOf('?') !== -1 ? '&' : '?') + 'hideBookmark=true';
                }

                if (options.params) {
                    var params = '';
                    $.each(options.params, function (name, value) {
                        if (params != '') {
                            params += '&';
                        }
                        params += name + '=' + value;
                    });
                    url += params ? '?' + params : '';
                }
                var newWindow = window.open(url, options.winname, 'top=' + options.top + ', left=' + options.left + ', width=' + options.width + ', height=' + options.height + ', ' + options.layout);

                // 새 창에서 북마크 버튼을 숨기기 위한 스타일 추가
                newWindow.addEventListener('load', function() {
                    if (options.hideBookmark) {
                        var style = newWindow.document.createElement('style');
                        style.innerHTML = '.bookmark.toggle { display: none; }';
                        newWindow.document.head.appendChild(style);
                    }
                });

                return newWindow;

            },
            delOtherTab: function () {
                nthTabs.find(".nav-tabs li").not('[class="active"]').not('[not-allow-close]').remove();
                nthTabs.find(".tab-content div.tab-pane").not('[not-allow-close]').not('[class$="active"]').remove();
                nthTabs.find('.content-tabs-container').css("margin-left", 40);
                return this;
            },

            delAllTab: function () {
                this.delOtherTab();
                this.delTab();
                return this;
            },

            setActTab: function (tabId) {
                tabId = tabId == undefined ? methods.getActiveId() : tabId;
                tabId = tabId.indexOf('#') > -1 ? tabId : '#' + tabId;
                nthTabs.find('.active').removeClass('active');
                nthTabs.find("[href='" + tabId + "']").parent().addClass('active');
                nthTabs.find(tabId).addClass('active');
                return this;
            },

            toggleTab: function (tabId) {
                this.setActTab(tabId).locationTab(tabId);
                return this;
            },

            isExistsTab: function (tabId) {
                tabId = tabId.indexOf('#') > -1 ? tabId : '#' + tabId;
                return nthTabs.find(tabId).length > 0;
            },

            tabToggleHandler: function (func) {
                handler["tabToggleHandler"] = func;
            }
        };

        var event = {
            onWindowsResize: function () {
                $(window).resize(function () {
                    settings.rollWidth = nthTabs.width() - 120;
                });
                return this;
            },

            onLocationTab: function () {
                nthTabs.on("click", '.tab-location', function () {
                    methods.locationTab();
                });
                return this;
            },

            onTabClose: function () {
                nthTabs.on("click", '.tab-close', function () {
                    var tabId = $(this).parent().find("a").attr('href');
                    var navTabOpt = nthTabs.find("[href='" + tabId + "']");
                    if (navTabOpt.parent().next().length == 0) {
                        var beforeTabsWidth = 0;
                        navTabOpt.parent().prevAll().each(function () {
                            beforeTabsWidth += $(this).width();
                        });
                        var optTabWidth = navTabOpt.parent().width();
                        var margin_left_total = 40;
                        var contentTab = navTabOpt.parent().parent().parent();
                        if (beforeTabsWidth > settings.rollWidth) {
                            var margin_left_origin = contentTab.css('marginLeft').replace('px', '');
                            margin_left_total = parseFloat(margin_left_origin) + optTabWidth + 40;
                        }
                        contentTab.css("margin-left", margin_left_total);
                    }
                    methods.delTab(tabId);
                });
                return this;
            },

            onTabCloseOpt: function () {
                nthTabs.on("click", '.tab-close-current', function (e) {
                    e.preventDefault();
                    var _tabdelid = $(this).closest('ul').attr('targetObjId');
                    _tabdelid = _tabdelid.slice(1, _tabdelid.length);
                    methods.delTab(_tabdelid);
                });
                return this;
            },

            onTabNewWindow: function () {
                nthTabs.on("click", '.tab-open-current', function (e) {
                    e.preventDefault();
                    var targetObjId = $(this).closest('ul').attr('targetObjId');

                    // openNewWindow를 사용하여 새 창을 엶
                    methods.openNewWindow(targetObjId);

                });
                return this;
            },

            onTabCloseOther: function () {
                nthTabs.on("click", '.tab-close-other', function (e) {
                    e.preventDefault();
                    methods.delOtherTab();
                });
                return this;
            },

            onTabCloseAll: function () {
                nthTabs.on("click", '.tab-close-all', function (e) {
                    e.preventDefault();
                    methods.delAllTab();
                });
                return this;
            },

            onTabRollLeft: function () {
                nthTabs.on("click", '.roll-nav-left', function () {
                    var contentTab = $(this).parent().find('.content-tabs-container');
                    var margin_left_total;
                    if (methods.getAllTabWidth() <= settings.rollWidth) {
                        margin_left_total = 40;
                    } else {
                        var margin_left_origin = contentTab.css('marginLeft').replace('px', '');
                        margin_left_total = parseFloat(margin_left_origin) + methods.getMarginStep() + 40;
                    }
                    contentTab.css("margin-left", margin_left_total > 40 ? 40 : margin_left_total);
                });
                return this;
            },

            onTabRollRight: function () {
                nthTabs.on("click", '.roll-nav-right', function () {
                    if (methods.getAllTabWidth() <= settings.rollWidth) return false;
                    var contentTab = $(this).parent().find('.content-tabs-container');
                    var margin_left_origin = contentTab.css('marginLeft').replace('px', '');
                    var margin_left_total = parseFloat(margin_left_origin) - methods.getMarginStep();
                    if (methods.getAllTabWidth() - Math.abs(margin_left_origin) <= settings.rollWidth) return false;
                    contentTab.css("margin-left", margin_left_total);
                });
                return this;
            },

            onTabList: function () {
                return this;
            },

            onTabListToggle: function () {
                nthTabs.on("click", '.toggle-tab', function () {
                    var tabId = $(this).data("id");
                    methods.setActTab(tabId).locationTab(tabId);
                });
                nthTabs.on('click', '.scroll-element', function (e) {
                    e.stopPropagation();
                });
                return this;
            },

            onTabToggle: function () {
                nthTabs.on("click", '.nav-tabs li', function () {
                    $('.gnb ul li a').removeClass('on');
                    $('li.has_sub2').removeClass('open');
                    $('li.has_sub2 > ul').slideUp(200);
                    if (delflag == false) {
                        var _objIdPrev = methods.getActiveId();
                        var $parentPrev = $('#menu').find('a[data-objid="' + _objIdPrev + '"]').closest('li.has_sub');
                        $parentPrev.removeClass('open');
                        $parentPrev.children('ul').hide();

                        var _objId = $(this).children('a').prop('hash').replace('#', '');
                        var $parentLi = $('#menu').find('a[data-objid="' + _objId + '"]').closest('li.has_sub');
                        $parentLi.addClass('open');
                        $parentLi.children('ul').show();
                        $('#menu').find('a[data-objid="' + _objId + '"]').addClass('on');

                        var $parentLisub = $('#menu').find('a[data-objid="' + _objId + '"]').closest('li.has_sub2');
                        if ($parentLisub.length > 0) {
                            $parentLisub.addClass('open');
                            $parentLisub.children('ul').slideDown(200);
                        }
                    } else {
                        var $parentLi = $('#menu').find('a[data-objid="' + methods.getActiveId() + '"]').closest('li.has_sub');
                        $parentLi.addClass('open');
                        $parentLi.children('ul').show();
                        $('#menu').find('a[data-objid="' + methods.getActiveId() + '"]').addClass('on');
                    }
                    delflag = false;

                    var lastTabText = nthTabs.find(".nav-tabs li a[href='#" + methods.getActiveId() + "'] span").text();
                    handler.hasOwnProperty("tabToggleHandler") && handler["tabToggleHandler"]({
                        last: {
                            tabId: methods.getActiveId(),
                            tabText: lastTabText
                        },
                        active: {
                            tabId: $(this).find("a").attr("href").replace('#', ''),
                            tabText: $(this).find("a span").text()
                        }
                    });
                });
            }
        };
        return run();
    }
})(jQuery);
