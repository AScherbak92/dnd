package by.dnd.cipher.controllers;

import by.dnd.cipher.repository.entity.CypheredEntityDTO;
import by.dnd.cipher.services.AdminPanelService;
import by.dnd.cipher.services.EntityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MainController {
    private final EntityService entityService;
    private final AdminPanelService adminPanelService;

    public MainController(EntityService entityService, AdminPanelService adminPanelService) {
        this.entityService = entityService;
        this.adminPanelService = adminPanelService;
    }

    /* GET
        Return MainPage.html
     */
    @GetMapping("/")
    public String mainPage(){
        return "MainPage";
    }

    /* GET
        Return CipherPage.html
     */
    @GetMapping("/cipher")
    public String getCipher(HttpSession session, Model model) {
        String accessKey = (String) session.getAttribute("accessKey");

        if(accessKey == null || !adminPanelService.keyCheck(accessKey)) {
            session.invalidate();
            return "redirect:/admin";
        }
        model.addAttribute("cypheredEntityDTO", new CypheredEntityDTO());

        return "CipherPage";
    }

    /* POST
        Create entity CypheredEntity in DB
     */
    @PostMapping("/createEntity")
    public String createEntity(@ModelAttribute("cypheredEntityDTO") CypheredEntityDTO dto, Model model){
        boolean created = entityService.createCypheredEntity(dto);

        if (!created) {
            model.addAttribute("error", "Невозможно перезаписать то, что уже существует" +
                    ", судьба - абсолютна");
        }

        model.addAttribute("cypheredEntityDTO", dto);

        return "CipherPage";
    }

    /* GET
        Return ResearchPage.html
     */
    @GetMapping("/research")
    public String getResearch(Model model) {
        model.addAttribute("cypheredEntityDTO", new CypheredEntityDTO());

        return "ResearchPage";
    }

    /* POST
        Returning a CypheredEntity to a user
     */
    @PostMapping("/searchEntity")
    public String searchForEntity(@ModelAttribute("cypheredEntityDTO") CypheredEntityDTO dto, Model model){
        CypheredEntityDTO searchedEntity = entityService.searchCypheredEntity(dto);

        if (searchedEntity.getEntityValue() == null) {
            model.addAttribute("error", "Такой записи не существует");
        }

        model.addAttribute("cypheredEntity", searchedEntity);

        return "ResearchPage";
    }

    /* GET
        Return AdminPage.html
     */
    @GetMapping("/admin")
    public String openAdminPage(){
        return "AdminPage";
    }

    /* POST
        Verifying key for admin panel
     */
    @PostMapping("/verifyKey")
    public String verifyAdminKey(@ModelAttribute("key") String key, Model model, HttpSession session){
        if(!adminPanelService.keyCheck(key)){
            model.addAttribute("error", "Invalid access key!");
            return "AdminPage";
        }

        session.setAttribute("accessKey", key);
        model.addAttribute("keyValid", true);

        return "AdminPage";
    }

    /* GET
        Return a list of records for admin
     */
    @GetMapping("/showRecords")
    public String showRecords(Model model, HttpSession session){
        String accessKey = (String) session.getAttribute("accessKey");

        if(accessKey == null || !adminPanelService.keyCheck(accessKey)) {
            session.invalidate();
            return "redirect:/admin";
        }

        List<CypheredEntityDTO> entities = entityService.findAllEntities();
        model.addAttribute("allRecords", entities);
        model.addAttribute("keyValid", true);

        return "AdminPage";
    }

    /* DELETE
        Delete record from database
    */
    @PostMapping("/deleteRecord")
    public String deleteRecord(@ModelAttribute("entityKey") String key, HttpSession session, Model model) {
        String accessKey = (String) session.getAttribute("accessKey");

        if(accessKey == null || !adminPanelService.keyCheck(accessKey)) {
            session.invalidate();
            return "redirect:/admin";
        }

        entityService.deleteEntity(key);
        model.addAttribute("keyValid", true);

        return "AdminPage";
    }

    /* GET
        View a certain record value from admin page
     */
    @GetMapping("/viewRecord")
    public String viewRecord(@RequestParam("key") String key, HttpSession session, Model model) {
        String accessKey = (String) session.getAttribute("accessKey");

        if(accessKey == null || !adminPanelService.keyCheck(accessKey)) {
            session.invalidate();
            return "redirect:/admin";
        }

        CypheredEntityDTO entity = entityService.searchCypheredEntity(new CypheredEntityDTO(key, null));

        model.addAttribute("entity", entity);
        model.addAttribute("keyValid", true);

        return "ViewRecordPage";
    }
}
