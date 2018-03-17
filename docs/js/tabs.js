$(function(){
    $('.responsive-tabs .nav-next, .nav-prev').click(function() {
        let navTabs = $(this).parents().find('.nav-tabs:first');
        let activeTab = navTabs.find('.nav-link.active').parent();
        let nextTab;

        if ($(this).hasClass('nav-next')) {
            nextTab = activeTab.next('.nav-item');
        } else if($(this).hasClass('nav-prev')) {
            nextTab = activeTab.prev('.nav-item');
        } else {
            return;
        }

        if (nextTab.length > 0) {
            let nextTabId = nextTab.find('.nav-link').attr('id');

            activeTab.removeClass('active');
            nextTab.addClass('active');
            $('#' + nextTabId).tab('show');
        }
    });
});