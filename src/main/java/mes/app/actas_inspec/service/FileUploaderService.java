package mes.app.actas_inspec.service;


import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileUploaderService {


    //파일 다중 업로드
    public Map<String, Object> saveFiles(MultipartFile file, String path) throws IOException {

            Map<String, Object> fileinformList = new HashMap<>();

                String fileName = file.getOriginalFilename();
                String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                String fileUuidName = UUID.randomUUID().toString() + "." + ext;
                String saveFilePath = path;
                File saveDir = new File(saveFilePath);

                Float fileSize = (float) file.getSize();


                if(!saveDir.isDirectory()){
                    saveDir.mkdirs();
                }

                File saveFile = new File(path + File.separator + fileUuidName);
                file.transferTo(saveFile);

                fileinformList.put("saveFilePath", saveFilePath);
                fileinformList.put("file_uuid_name", fileUuidName);
                fileinformList.put("ext", ext);
                fileinformList.put("fileName", fileName);
                fileinformList.put("fileSize", fileSize);

            return fileinformList;
    }


    public ResponseEntity<Resource> downloadFile(String fileName, String filepath){
        Path path = Paths.get(filepath, fileName);
        Resource resource = new FileSystemResource(path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);

    }


}
