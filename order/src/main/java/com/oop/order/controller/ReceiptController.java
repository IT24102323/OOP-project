package com.oop.order.controller;

import com.oop.order.model.Receipt;
import com.oop.order.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("receipt", new Receipt());
        return "create-receipt";
    }

    @PostMapping("/create")
    public String handleCreate(@ModelAttribute Receipt receipt) {
        receipt.setId(receiptService.generateUniqueId());
        receipt.setDateTime(java.time.LocalDateTime.now());
        receiptService.saveReceipt(receipt);
        return "redirect:/receipts";
    }

    @GetMapping
    public String viewAllReceipts(Model model) {
        model.addAttribute("receipts", receiptService.getAllReceipts());
        return "list-receipts";
    }

    @GetMapping("/view/{id}")
    public String viewReceipt(@PathVariable String id, Model model) {
        Receipt receipt = receiptService.getReceiptById(id);
        if (receipt == null) {
            return "redirect:/receipts";
        }
        model.addAttribute("receipt", receipt);
        return "view-receipt";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Receipt receipt = receiptService.getReceiptById(id);
        if (receipt == null) {
            return "redirect:/receipts";
        }
        model.addAttribute("receipt", receipt);
        return "edit-receipt";
    }

    @PostMapping("/update")
    public String handleUpdate(@ModelAttribute Receipt receipt) {
        receiptService.updateReceipt(receipt);
        return "redirect:/receipts/view/" + receipt.getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteReceipt(@PathVariable String id) {
        receiptService.deleteReceipt(id);
        return "redirect:/receipts";
    }
}