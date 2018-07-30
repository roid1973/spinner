package junit.spinnerCalendarTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import spinnerCalendar.SpinnerCalendarServices;
import spinnerCalendar.Status;
import spinnerClass.SpinnerClass;
import spinnerClass.SpinnerClassInputRequest;
import utils.SpinnerConstants;

public class ClassDatastoreTest {

	String className = "Junit Test Class";

	@Test
	public void basicOperations() throws Exception {
		System.out.println("Test: basicOperations");
		SpinnerClass sc;
		sc = addClass(className);
		int classId = sc.getId();
		getClass(className);
		getClass(classId);
		updateClass(sc);
		deleteClass(className);
		sc = addClass(className);
		deleteClass(sc.getId());
	}

	@Test
	public void getSpinnerClassListFromDatastoreTest() throws Exception {
		System.out.println("Test: getSpinnerClassListFromDatastoreTest");
		addClass(className);
		SpinnerCalendarServices scs = new SpinnerCalendarServices();
		assertNotNull(scs.getSpinnerClassListFromDatastore());
		deleteClass(className);
	}

	@Test
	public void getNonExistClassFromDatasource() throws Exception {
		System.out.println("Test: getNonExistClassFromDatasource");
		SpinnerCalendarServices scs = new SpinnerCalendarServices();
		SpinnerClass result = scs.getClassByName("XXXX");
		assertNull(result);
		result = scs.getClassByID(-99);
		assertNull(result);
	}

	@Test
	public void multiThreadAddClass() throws Exception {
		System.out.println("Test: multiThreadAddClass");
		ExecutorService executorService = new ThreadPoolExecutor(5, 5, 600, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		Runnable runnableTask = new Runnable() {
			@Override
			public void run() {
				System.out.println("start thread number: " + Thread.currentThread().getId());
				try {
					String className = "TestMultiThread" + new Random().nextInt();
					SpinnerClass sc = addClass(className);
					updateClass(sc);
					int classId = (int) sc.getId();
					deleteClass(classId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("end thread number: " + Thread.currentThread().getId());
			}
		};

		for (int i = 0; i < 10; i++) {
			executorService.execute(runnableTask);
		}
		Thread.sleep(9000);
	}

	private void getClass(String className) throws Exception {
		SpinnerCalendarServices scs = new SpinnerCalendarServices();
		SpinnerClass result = scs.getClassByName(className);
		assertNotNull(result);
		System.out.println("class found: " + result.getSpinnerClassName());
		assertEquals(className, result.getSpinnerClassName());
	}

	private void getClass(int classId) throws Exception {
		SpinnerCalendarServices scs = new SpinnerCalendarServices();
		SpinnerClass result = scs.getClassByID(classId);
		assertNotNull(result);
		System.out.println("class found: " + result.getId());
		assertEquals(classId, result.getId());
	}

	private void updateClass(SpinnerClass spinnerClass) throws Exception {
		int lockForRegistrationNewValue = 100;
		SpinnerCalendarServices scs = new SpinnerCalendarServices();
		SpinnerClassInputRequest input = new SpinnerClassInputRequest();
		input.setClassName(spinnerClass.getSpinnerClassName());
		input.setHyperLink("");
		input.setLockForRegistration(lockForRegistrationNewValue);
		input.setOpenForRegistrationMode(SpinnerConstants.MONTHLY_STRING);
		List<SpinnerClassInputRequest> sc = new ArrayList<SpinnerClassInputRequest>();
		sc.add(input);
		int classId = spinnerClass.getId();
		SpinnerClass result = scs.updateClass(classId, sc);
		assertNotNull(result);
		System.out.println("class lockForRegistration updated succesfuly to: " + result.getLockForRegistration());
		assertEquals(lockForRegistrationNewValue, result.getLockForRegistration());
	}

	private SpinnerClass addClass(String className) throws Exception {
		SpinnerClassInputRequest input = new SpinnerClassInputRequest();
		input.setClassName(className);
		input.setHyperLink("");
		input.setLockForRegistration(60);
		input.setOpenForRegistrationMode(SpinnerConstants.MONTHLY_STRING);
		SpinnerClass result = (new SpinnerCalendarServices()).addClassToDatasource(input);
		assertNotNull(result);
		System.out.println("class added succesfuly: " + result.getSpinnerClassName());
		assertEquals(className, result.getSpinnerClassName());
		return result;
	}

	private void deleteClass(String className) throws Exception {
		SpinnerCalendarServices scs = new SpinnerCalendarServices();
		try {
			Status sts = scs.deleteClassByName(className);
			System.out.println("class deleted succesfuly: " + className);
			assertEquals(Status.CLASS_DELETED, sts.getStatus());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void deleteClass(int classId) throws Exception {
		SpinnerCalendarServices scs = new SpinnerCalendarServices();
		try {
			Status sts = scs.deleteClassByID(classId);
			System.out.println("class deleted succesfuly: " + classId);
			assertEquals(Status.CLASS_DELETED, sts.getStatus());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
