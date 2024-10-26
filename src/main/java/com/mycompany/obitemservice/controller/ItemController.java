package com.mycompany.obitemservice.controller;

import com.mycompany.obitemservice.model.ItemModel;
import com.mycompany.obitemservice.repository.ItemRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/welcome")
    public String welcome() {
        return "Breaking Changes!!";
    }

    @GetMapping("/items")
    public List<ItemModel> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/items/{id}")
    public ItemModel getItem(@PathVariable String id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot Find Item By ID: " + id));
    }

    @PostMapping("/items")
    public ResponseEntity<String> saveItem(@RequestBody ItemModel item) {
        ItemModel savedItem = itemRepository.insert(item);//it will create new document in table with autogenerated id, if id exist than exception in is thrown
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedItem.getId())
                .toUri();
        //http://localhost:8081/api/v1/items/611b7bcfef59e87f2e0e0d60
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/items/{id}")//full update of all properties
    public ResponseEntity<ItemModel> updateItem(@PathVariable String id, @RequestBody ItemModel item) {
        ItemModel imFromDB = itemRepository.findById(id).orElseThrow(()->new RuntimeException("Cannot Find Item By ID: " + id));
        BeanUtils.copyProperties(item, imFromDB);//copy all data from item to imFromDB
        imFromDB = itemRepository.save(imFromDB);//if request has id than it will update else it will insert new document with new autogenerated id
        return new ResponseEntity<>(imFromDB, HttpStatus.OK);
    }

    @PatchMapping("/items/{id}")//only update price keep other fields as old values
    public ResponseEntity<ItemModel> updateItemPrice(@PathVariable String id, @RequestBody ItemModel item) {
        ItemModel imFromDB = itemRepository.findById(id).orElseThrow(()->new RuntimeException("Cannot Find Item By ID: " + id));
        imFromDB.setPrice(item.getPrice());
        imFromDB = itemRepository.save(imFromDB);//if request has id than it will update else it will insert new document with new autogenerated id
        return new ResponseEntity<>(imFromDB, HttpStatus.OK);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable String id) {
        itemRepository.deleteById(id);
        ResponseEntity<String> re = new ResponseEntity<>(id, HttpStatus.OK);
        return re;
    }
}
