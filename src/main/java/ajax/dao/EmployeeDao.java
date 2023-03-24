package ajax.dao;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import ajax.entity.Employee;

public class EmployeeDao {
	
	private EntityManagerFactory entityManagerFactory;
	
	public EmployeeDao(ServletContext servletContext) {
		entityManagerFactory = (EntityManagerFactory)servletContext.getAttribute("entityManagerFactory");
	}
	
	public List<Employee> getAllEmployees() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query = entityManager.createQuery("SELECT employee FROM Employee employee");
        List<Employee> employees = query.getResultList();
        entityManager.close();              
        return employees;
	}
	
	public Employee getEmployeeById(Integer id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
	        
		Employee employee = entityManager.find(Employee.class, id);
	        
	    entityManager.close();              
	    return employee;
	}
	
	public void addEmployee(Employee employee) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction etx = entityManager.getTransaction();
        etx.begin();
        entityManager.persist(employee);
        etx.commit();
        entityManager.close();
	}
	
	public void updateEmployee(Integer id, Employee employee) {
	    Employee existingEmployee = getEmployeeById(id);
	    
	    if(existingEmployee == null) {
	        return;
	    }

	    // 使用反射比較每個欄位的值
	    Field[] fields = Employee.class.getDeclaredFields();
	    for (Field field : fields) {
	        try {
	            // 確認欄位是否有 @Column 註解
	            if (field.isAnnotationPresent(Column.class)) {
	                // 設定欄位可存取
	                field.setAccessible(true);
	                Object newValue = field.get(employee);
	                Object oldValue = field.get(existingEmployee);
	                if (newValue != null && !newValue.equals(oldValue)) {
	                    field.set(existingEmployee, newValue);
	                }
	            }
	        } catch (IllegalAccessException e) {
	            // 無法存取欄位
	            e.printStackTrace();
	        }
	    }

	    EntityManager entityManager = entityManagerFactory.createEntityManager();
	    EntityTransaction etx = entityManager.getTransaction();
	    etx.begin();
	    entityManager.merge(existingEmployee);
	    etx.commit();
	    entityManager.close();
	}
	
	public void deleteEmployee(Integer id) {
	    EntityManager entityManager = entityManagerFactory.createEntityManager();
	    EntityTransaction etx = entityManager.getTransaction();
	    etx.begin();
	    // 注意在進行刪除的時候要把查找也放到 etx 環境中避免斷開連接的實體
	    Employee deleteEmp = entityManager.find(Employee.class, id);
	    if(deleteEmp != null) {
	        entityManager.remove(deleteEmp);
	    }
	    etx.commit();
	    entityManager.close();
	}
	
}
