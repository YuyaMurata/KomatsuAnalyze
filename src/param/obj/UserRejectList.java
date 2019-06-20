/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package param.obj;

import file.ListToCSV;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17807
 */
public class UserRejectList {
    private Stream<Path> paths;
    public UserRejectList(String path) {
        try {
            paths = Files.list(Paths.get(path));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void reject(Map<String, SyaryoObject> map){
        List<String> rejlist = paths.map(f -> ListToCSV.toList(f.toAbsolutePath().toString()))
                                    .flatMap(fl -> fl.stream())
                                    .distinct()
                                    .collect(Collectors.toList());
        
        System.out.println("除外車両数："+rejlist.stream().filter(sid -> map.get(sid) != null).count());
        rejlist.stream().forEach(map::remove);
    }
}
