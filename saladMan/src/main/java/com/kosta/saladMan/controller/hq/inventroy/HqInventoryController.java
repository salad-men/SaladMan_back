//package com.kosta.saladMan.controller.hq.inventroy;
//
//
//import com.kosta.saladMan.service.inventory.IngredientService;
//import com.kosta.saladMan.service.inventory.IngredientSettingService;
//import com.kosta.saladMan.service.inventory.InventoryExpirationService;
//import com.kosta.saladMan.util.PageInfo;
//import com.kosta.saladMan.dto.inventory.IngredientDto;
//import com.kosta.saladMan.service.inventory.DisposalService;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/hq/inventory")
//@RequiredArgsConstructor
//public class HqInventoryController {
//
//    private final IngredientService ingredientService;
//    private final IngredientSettingService ingredientSettingService;
//    private final InventoryExpirationService inventoryExpirationService;
//    private final DisposalService disposalService;
//
//    // 카테고리 목록 조회
//    @GetMapping("/categories")
//    public ResponseEntity<List<String>> getCategories() {
//        try {
//            List<String> categories = ingredientService.getAllCategories();
//            return new ResponseEntity<>(categories, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // 지점 목록 조회 
//    @GetMapping("/stores")
//    public ResponseEntity<List<String>> getStores() {
//        try {
//            List<String> stores = ingredientService.getAllStores();
//            return new ResponseEntity<>(stores, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // 재고 목록 조회 
//    @RestController
//    @RequestMapping("/hq/inventory")
//    @RequiredArgsConstructor
//    public class InventoryController {
//
//        private final IngredientService ingredientService;
//
//        @PostMapping("/list")
//        public ResponseEntity<Map<String,Object>> list(@RequestBody(required=false) Map<String,String> param) {
//            String store = "all";
//            String category = "all";
//            String name = "";
//            PageInfo pageInfo = new PageInfo(1);
//
//            if(param != null) {
//                if(param.get("page") != null) {
//                    pageInfo.setCurPage(Integer.parseInt(param.get("page")));
//                }
//                if(param.get("store") != null) {
//                    store = param.get("store");
//                }
//                if(param.get("category") != null) {
//                    category = param.get("category");
//                }
//                if(param.get("name") != null) {
//                    name = param.get("name");
//                }
//            }
//
//            try {
//                List<IngredientDto> ingredientList = ingredientService.searchIngredientList(pageInfo, store, category, name);
//                Map<String,Object> res = new HashMap<>();
//                res.put("inventoryList", ingredientList);
//                res.put("pageInfo", pageInfo);
//                return new ResponseEntity<>(res, HttpStatus.OK);
//            } catch(Exception e) {
//                e.printStackTrace();
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//        }
//    }
//
//
//    // 재고 상세 조회 (IngredientService)
//    @GetMapping("/detail")
//    public ResponseEntity<IngredientDto> getInventoryDetail(@RequestParam Integer id) {
//        try {
//            IngredientDto dto = ingredientService.getIngredientById(id);
//            return new ResponseEntity<>(dto, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    // 재고 정보 수정 (IngredientService)
//    @PutMapping("/update")
//    public ResponseEntity<Void> updateInventory(@RequestBody IngredientDto dto) {
//        try {
//            ingredientService.updateIngredient(dto);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//    
//    // 추가로 IngredientSetting, InventoryExpiration, Disposal 관련 API도 추가 가능
//    // 예를 들어:
//    // @GetMapping("/settings") public ResponseEntity<?> getSettings() { ... }
//    // @PostMapping("/disposals") public ResponseEntity<?> addDisposal(...) { ... }
//}
