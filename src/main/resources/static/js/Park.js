


let AjaxFunction = {
    PostAjaxRequest: function(url, async, contentType, processData, bodyData, successCallback, errorCallback){
        $.ajax({
            url: url,
            type: 'POST',
            contentType: contentType,
            async: async,   // 기본값: true
            data: bodyData,
            processData: processData,   //기본값: true
            headers: {
                'X-CSRF-Token': csrfToken
            },
            success: function(res){
                successCallback(res);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                errorCallback(jqXHR, textStatus, errorThrown);
            }
        });
    }
};

// 로딩 바를 보여주는 함수
function showLoadingBar() {
    document.getElementById('loadingBar').style.display = 'block';
}

// 로딩 바를 숨기는 함수
function hideLoadingBar() {
    document.getElementById('loadingBar').style.display = 'none';
}


function addInputField(button) {
    // 새로운 td 요소를 생성합니다.
    var newTd = document.createElement('div');
    newTd.className = 'input-btnbox';
    newTd.name = 'newusrbox';

    // 새로운 input 요소를 생성합니다.
    var newInput = document.createElement('input');
    newInput.type = 'text';
    newInput.className = 'wp100 addusr';
    newInput.placeholder = '점검자';
    newInput.name = 'checkusr';

    newInput.maxLength = 5;

    // 삭제 버튼을 생성합니다.
    var deleteButton = document.createElement('button');
    deleteButton.textContent = '점검자 삭제';
    deleteButton.onclick = function() {
        deleteInputField(newInput); // 삭제 버튼을 클릭하면 해당 입력 필드를 삭제하는 함수 호출
    };
    deleteButton.style.marginLeft = '8px'; // 삭제 버튼에 직접 style 속성을 사용하여 margin-left을 설정합니다.
    deleteButton.style.color = 'red';
    deleteButton.style.marginTop = '5px';

    // 새로운 td에 input 요소와 삭제 버튼을 추가합니다.
    newTd.appendChild(newInput);
    newTd.appendChild(deleteButton);

    // 클릭된 버튼의 부모 td 요소를 찾습니다.
    var parentTd = button.parentElement;

    // 부모 td 요소 다음에 새로운 td 요소를 추가합니다.
    parentTd.parentElement.insertBefore(newTd, parentTd.nextSibling);
}


//하나씩만 삭제
function deleteInputField(inputField) {
    // 입력 필드의 부모 요소(td)를 찾아서 삭제합니다.
    var tdToDelete = inputField.parentElement;
    tdToDelete.parentElement.removeChild(tdToDelete);
}






// 파일 리스트 UI 업데이트 함수
function updateFileListUI() {
    const $fileList = $('#filelist');

    uploadedFiles.forEach(file => {
        const fileSize = (file.size / 1024).toFixed(2) + ' KiB';
        const li = $('<li>').html(`
                    <p>${file.name} <span>(${fileSize})</span></p>
                    <a href="#" title="삭제" class="btn-file-delete">
                        <img src="/images/icon/ico-filedelete.svg" alt="삭제아이콘">
                    </a>
                `);
        $fileList.append(li);
    });
}


function updateTabLink(){
    const spuncode = document.getElementById('spuncode').value;
    const inputTabLink = document.getElementById('inputTabLink');

    if(spuncode){
        inputTabLink.textContent = '수정';
        inputTabLink.title = '수정'
    }else {
        inputTabLink.textContent = '등록';
        inputTabLink.title = '등록';
    }
}


function postFetchRequest(url, data, successCallback, errorCallback) {
    fetch(url, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": csrfToken
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                return response.blob().then(blob => {
                    if (response.status === 404) {
                        throw new Error('첨부된 파일이 없습니다.');
                    } else {
                        throw new Error('Network Error.');
                    }
                });
            }
            return response.blob();
        })
        .then(blob => {
            successCallback(blob);
        })
        .catch(error => {
            errorCallback(error);
        })
        .finally(() => {
            hideLoadingBar();
        })
}




$(document).ready(function (e) {
    //점검결과 클릭시 텍스트 순환
    $('.checkbox-cell').click(function() {
        const span = $(this).find('span');
        if (span.text() === 'X') {
            span.text('O');
        } else if (span.text() === 'O') {
            span.text('X');
        }
    });


})


