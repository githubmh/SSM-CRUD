package com.atguigu.crud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.crud.bean.Employee;
import com.atguigu.crud.bean.Msg;
import com.atguigu.crud.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * ����Ա��CRUD����
 * @author Administrator
 *
 */
@Controller
public class EmployeeController {
	@Autowired
	EmployeeService employeeService;
	
	/**
	 * Ա��ɾ������
	 * ������������һ
	 * @param employee
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
	public Msg deleteEmp(@PathVariable("ids")String ids){
		if(ids.contains("-")){
			//����ɾ��
			List<Integer> del_ids = new ArrayList<>();
			String[] str_ids = ids.split("-");
			//��װID�ļ���
			for (String string : str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			employeeService.deleteBatch(del_ids);
		}else{
			//����ɾ��
			Integer id = Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}
		return Msg.success();
	}
	
	/**
	 * Ա�����·���
	 * @param employee
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{empId}",method=RequestMethod.PUT)
	public Msg saveEmp(Employee employee){
		employeeService.updateEmp(employee);
		return Msg.success();
	}
	
	/**
	 * ����ID��ѯԱ��
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/emp/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id") Integer id){
		Employee employee = employeeService.getEmp(id);
		return Msg.success().add("emp", employee);
	}
	
	
	/**
	 * ����û����Ƿ����
	 * @param empName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkuser")
	public Msg checkuser(@RequestParam("empName")String empName){
		//���ж��û����Ƿ��ǺϷ��ı��ʽ
		String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
		if(!empName.matches(regx)){
			return Msg.fail().add("va_msg", "^�û���������2-5λ���Ļ�����6-16λӢ�ĺ����ֵ����");
		}
		//���ݿ��û��ظ�У��
		boolean b = employeeService.checkUser(empName);
		if(b){
			return Msg.success();
		}else{
			return Msg.fail().add("va_msg", "�û���������");
		}
	}
	
	/**
	 * Ա������
	 * ֧��JSR303У��
	 * ����hibernate-validator
	 * @return
	 */
	@RequestMapping(value="/emp",method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Employee employee,BindingResult result){
		if(result.hasErrors()){
			//У��ʧ��,Ӧ�÷���ʧ�ܣ���ģ̬������ʾʧ�ܵĴ�����Ϣ
			Map<String, Object> map = new HashMap<>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				//System.out.println("������ֶ�����"+fieldError.getField());
				//System.out.println("������Ϣ��"+fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
		}else {
			employeeService.saveEmp(employee);
			return Msg.success();
		}
	}
	
	//�赼��json��
	@RequestMapping("/emps")
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn",defaultValue="1") Integer pn){
		//����PageHelper
				PageHelper.startPage(pn,5);
				
				List<Employee> emps = employeeService.getAll();
				PageInfo page = new PageInfo(emps, 5);
				return Msg.success().add("pageInfo", page);
	}
	
	/**
	 * ��ѯԱ�����ݣ���ҳ��ѯ��
	 * @return
	 */
	//@RequestMapping("/emps")
	public String getEmps(@RequestParam(value="pn",defaultValue="1")Integer pn,
			Model model){
		//����PageHelper
		PageHelper.startPage(pn,5);
		
		List<Employee> emps = employeeService.getAll();
		PageInfo page = new PageInfo(emps,5);
		model.addAttribute("pageInfo", page);
		
		return "list";
	} 
}
