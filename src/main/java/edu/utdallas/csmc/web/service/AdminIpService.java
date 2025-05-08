package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminIpResultDTO;
import edu.utdallas.csmc.web.helper.AdminIpHelper;
import edu.utdallas.csmc.web.model.misc.IpAddress;
import edu.utdallas.csmc.web.repository.IpAddressRepository;
import edu.utdallas.csmc.web.util.IpChecker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class AdminIpService {
    @Autowired
    private IpAddressRepository ipAddressRepository;

    AdminIpHelper adminIpHelper =new AdminIpHelper();

    public ModelMap getIpDetails(ModelMap model){
        List<IpAddress> csmc = new ArrayList<>();
        List<IpAddress> csdept = new ArrayList<>();
        List<IpAddress> utd = new ArrayList<>();
        List<IpAddress> others = new ArrayList<>();

        List<IpAddress> ips = ipAddressRepository.findAll();
        for(IpAddress ip : ips) {
            if (ip.getRoom() != null) {
                csmc.add(ip);
            }
            else if (IpChecker.inRange(IpChecker.CS_DEPT_MASK, ip)) {
                csdept.add(ip);
            }
            else if (IpChecker.inRange(IpChecker.UTD_MASK, ip)) {
                utd.add(ip);
            }
            else {
                others.add(ip);
            }
        }

        List<AdminIpResultDTO> csmcDTO = adminIpHelper.setIpDetails(csmc);
        List<AdminIpResultDTO> csdeptDTO = adminIpHelper.setIpDetails(csdept);
        List<AdminIpResultDTO> utdDTO = adminIpHelper.setIpDetails(utd);
        List<AdminIpResultDTO> othersDTO = adminIpHelper.setIpDetails(others);

        model.addAttribute("csmc", csmcDTO);
        model.addAttribute("utdcs", csdeptDTO);
        model.addAttribute("utd", utdDTO);
        model.addAttribute("others", othersDTO);

        return model;
    }

    public String submitIpDetails(AdminIpResultDTO adminIpResultDTO) {
        String message ="";
        IpAddress ip = adminIpHelper.setNewIp(adminIpResultDTO);
        Optional<IpAddress> address = ipAddressRepository.findByAddress(ip.getAddress());
        if(address.isPresent()) {
            message ="Ip Already Exists";
        }else{
            ipAddressRepository.save(ip);
            log.info("Ip Created");
        }
        return message;
    }

    public AdminIpResultDTO getIpAdressDetails(String ipId) {
        AdminIpResultDTO adminIpResultDTO = new AdminIpResultDTO();
        Optional<IpAddress> ip = ipAddressRepository.findById(UUID.fromString(ipId));
        if(ip.isPresent()){
            IpAddress editIp = ip.get();
            adminIpHelper.setEditIpDetails(adminIpResultDTO, editIp);
        }else{
            log.info("Ip Not Found");
        }
        return adminIpResultDTO;
    }

    public void updateIpDetails(AdminIpResultDTO adminIpResultDTO) {
        Optional<IpAddress> ip = ipAddressRepository.findById(adminIpResultDTO.getIpId());

        if(ip.isPresent()){
            IpAddress currentIp = ip.get();
            adminIpHelper.updateIp(currentIp,adminIpResultDTO);
            ipAddressRepository.saveAndFlush(currentIp);
        }else{
            log.info("Ip not found");
        }
    }

    public String deleteIp(UUID ipId) {
        Optional<IpAddress> ip = ipAddressRepository.findById(ipId);
        if (ip.isPresent()) {
            try {
                ipAddressRepository.delete(ip.get());

            } catch (ConstraintViolationException ex) {
                log.error(ex);
                return "redirect:/admin/ip/edit/" + ipId;
            }
        } else {
            log.info("Ip not found for id " + ipId.toString());
        }
        return "redirect:/admin/ip";
    }
}
