package com.jedlab.pm.web.rest;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jedlab.framework.spring.SpringUtil;
import com.jedlab.framework.spring.rest.ResponseMessage;

@Controller
public class GlobalResource
{

    private static final Logger logger = Logger.getLogger(GlobalResource.class.getName());

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/notfound")
    public ResponseMessage notFound()
    {
        return new ResponseMessage(SpringUtil.getMessage("Not_Found", null), 200);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/loginFailed")
    public ResponseMessage loginFailed()
    {
        return new ResponseMessage(SpringUtil.getMessage("Login_Failed", null), 200);
    }

    /////////////////////// TEST API

    @ResponseBody
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
            "text/plain" }, produces = "application/json", value = "/api/v1/test")
    public ResponseMessage testApi()
    {
        return new ResponseMessage("Tested", 200);
    }

    @ResponseBody
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
            "text/plain" }, produces = "application/json", value = "/test")
    public ResponseEntity<ResponseMessage> test()
    {   
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(SpringUtil.getMessage("Error", null), 100));
    }

    @ResponseBody
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
            "text/plain" }, produces = "application/json", value = "/preauth/test")
    public ResponseMessage preauth()
    {
        return new ResponseMessage("Preauth", 200);
    }

}
