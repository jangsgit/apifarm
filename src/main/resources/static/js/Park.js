let csrfToken = $('[name=_csrf]').val();

let AjaxFunction = {
    sendAjaxRequest: function(url, type, contentType, processData, successCallback, errorCallback){
    $.ajax({
        url: url,
        type: type,
        contentType: contentType,
        data: data,
        processData: processData,
        headers: {
            'X-CSRF-Token': csrfToken
        },
        success: successCallback,
        error: errorCallback
    });
}
}
