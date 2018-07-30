package datastore.spinner;

import java.util.HashMap;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import spinnerCalendar.Status;
import spinnerClass.SpinnerClass;
import spinnerClass.SpinnerClassInputRequest;
import utils.PropertiesUtils;

public class ClassDatastore {

	private String namespace = PropertiesUtils.getSpinnerProperties().getProperty("datastore.namespace");
	private int datastoreTxnRetries = Integer.parseInt(PropertiesUtils.getSpinnerProperties().getProperty("datastore.TxnRetries"));
	private String classkind = "class";
	private String rootClassKind = "rootEntity";
	private String rootClassEntityName = "classEntity";

	// Create an authorized Datastore service using Application Default Credentials.

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// Create a Key factory to construct keys associated with this project.
	private final KeyFactory classFactory = datastore.newKeyFactory().setNamespace(namespace).setKind(classkind).addAncestor(PathElement.of(rootClassKind, rootClassEntityName));
	private final KeyFactory rootEntityFactory = datastore.newKeyFactory().setNamespace(namespace).setKind(rootClassKind);
	private Key rootClassKey = rootEntityFactory.newKey(rootClassEntityName);

	public ClassDatastore() {
	}

	public HashMap<Integer, SpinnerClass> getSpinnerClassListFromDatastore() throws Exception {
		HashMap<Integer, SpinnerClass> spinnerClasses = new HashMap<Integer, SpinnerClass>();
		Query<Entity> query = Query.newEntityQueryBuilder().setNamespace(namespace).setKind(classkind).setFilter(PropertyFilter.hasAncestor(rootClassKey)).build();
		QueryResults<Entity> classes = datastore.run(query);
		while (classes.hasNext()) {
			Entity classEntity = classes.next();
			SpinnerClass sc = generateSpinnerClass(classEntity);
			spinnerClasses.put(sc.getId(), sc);
		}
		return spinnerClasses;
	}

	private SpinnerClass generateSpinnerClass(Entity classEntity) throws Exception {
		SpinnerClass sc = null;
		if (classEntity != null) {
			String className = classEntity.getString("className");
			int classId = (int) classEntity.getLong("classId");
			String openForRegistrationMode = classEntity.getString("openForRegistrationMode");
			int lockForRegistration = (int) classEntity.getLong("lockForRegistration");
			String hyperLink = classEntity.getString("hyperLink");
			sc = new SpinnerClass(className, openForRegistrationMode, lockForRegistration, hyperLink);
			sc.setId(classId);
		}
		return sc;
	}

	public SpinnerClass addClass(SpinnerClassInputRequest scInput) throws Exception {
		return generateSpinnerClass(addClass(scInput, 0));
	}

	private Entity addClass(SpinnerClassInputRequest scInput, int retryCounter) throws Exception {
		Transaction txn = datastore.newTransaction();
		Entity sc = null;
		try {
			if (getClassByName(txn, scInput.getClassName()) != null) {
				throw new Exception("class name already exist: " + scInput.getClassName());
			}
			Key classKey = datastore.allocateId(classFactory.newKey());
			long classId = getNewClassIdTx(txn);
			sc = Entity.newBuilder(classKey).set("classId", classId).set("className", scInput.getClassName()).set("hyperLink", scInput.getHyperLink()).set("lockForRegistration", scInput.getLockForRegistration()).set("openForRegistrationMode", scInput.getOpenForRegistrationMode()).build();
			txn.add(sc);
			txn.commit();
		} catch (com.google.cloud.datastore.DatastoreException e) {
			if (retryCounter < datastoreTxnRetries) {
				retryCounter = retryCounter + 1;
				return addClass(scInput, retryCounter);
			} else {
				throw e;
			}
		} finally

		{
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return sc;
	}

	private long getNewClassIdTx(Transaction txn) {
		Entity rootClass = txn.get(rootClassKey);
		long id = rootClass.getLong("classIdCounter") + 1;
		Entity updateRootClass = Entity.newBuilder(rootClassKey).set("classIdCounter", id).build();
		txn.put(updateRootClass);
		return id;
	}

	public SpinnerClass updateClass(int classId, SpinnerClassInputRequest scInput) throws Exception {
		return generateSpinnerClass(updateClass(classId, scInput, 0));
	}

	private Entity updateClass(int classId, SpinnerClassInputRequest scInput, int retryCounter) throws Exception {
		Transaction txn = datastore.newTransaction();
		Entity sc = null;
		Entity updateClass = null;
		try {
			sc = getClassByID(txn, classId);
			if (sc == null) {
				throw new Exception("class with this ID dose not exist: " + classId);
			}
			updateClass = Entity.newBuilder(datastore.get(sc.getKey())).set("className", scInput.getClassName()).set("hyperLink", scInput.getHyperLink()).set("lockForRegistration", scInput.getLockForRegistration()).set("openForRegistrationMode", scInput.getOpenForRegistrationMode()).build();
			txn.put(updateClass);
			txn.commit();
		} catch (com.google.cloud.datastore.DatastoreException e) {
			if (retryCounter < datastoreTxnRetries) {
				retryCounter = retryCounter + 1;
				updateClass(classId, scInput, retryCounter);
			} else {
				throw e;
			}
		} finally

		{
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return updateClass;
	}

	public Status deleteClass(String className) throws Exception {
		Status sts = new Status(Status.FAILURE);
		Transaction txn = datastore.newTransaction();
		try {
			Entity sc = getClassByName(txn, className);
			if (sc == null) {
				sts.setStatus(Status.CLASS_NOT_EXIST);
			}
			if (sc != null) {
				datastore.delete(sc.getKey());
				return (new Status(Status.CLASS_DELETED));
			}
		} finally

		{
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return sts;
	}

	public Status deleteClass(int classId) throws Exception {
		Status sts = new Status(Status.FAILURE);
		Transaction txn = datastore.newTransaction();
		try {
			Entity sc = getClassByID(txn, classId);
			if (sc == null) {
				sts.setStatus(Status.CLASS_NOT_EXIST);
			}
			if (sc != null) {
				datastore.delete(sc.getKey());
				return (new Status(Status.CLASS_DELETED));
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return sts;
	}

	public SpinnerClass getClassByName(String className) throws Exception {
		Entity sc = null;
		Transaction txn = datastore.newTransaction();
		try {
			sc = getClassByName(txn, className);
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return generateSpinnerClass(sc);
	}

	private Entity getClassByName(Transaction txn, String className) throws Exception {
		Query<Entity> query = Query.newEntityQueryBuilder().setNamespace(namespace).setKind(classkind).setFilter(CompositeFilter.and(PropertyFilter.eq("className", className), PropertyFilter.hasAncestor(rootClassKey))).build();
		Entity sc = null;
		QueryResults<Entity> classes = txn.run(query);
		if (classes.hasNext()) {
			sc = classes.next();
		}
		if (classes.hasNext()) {
			throw new Exception("cannot retrieve class by 'className' as class name is not unique: " + className);
		}
		return sc;
	}

	public SpinnerClass getClassByID(int classId) throws Exception {
		Entity sc = null;
		Transaction txn = datastore.newTransaction();
		try {
			sc = getClassByID(txn, classId);
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return generateSpinnerClass(sc);
	}

	private Entity getClassByID(Transaction txn, int classId) throws Exception {
		Query<Entity> query = Query.newEntityQueryBuilder().setNamespace(namespace).setKind(classkind).setFilter(CompositeFilter.and(PropertyFilter.eq("classId", classId), PropertyFilter.hasAncestor(rootClassKey))).build();
		Entity sc = null;
		QueryResults<Entity> classes = txn.run(query);
		if (classes.hasNext()) {
			sc = classes.next();
		}
		if (classes.hasNext()) {
			throw new Exception("cannot retrieve class by 'classId' as class ID is not unique: " + classId);
		}
		return sc;
	}

	// TODO: delete after datastore transformation complete
	public SpinnerClass classTransformation(SpinnerClass spinnerClass) throws Exception {
		return generateSpinnerClass(classTransformation(spinnerClass, 0));
	}

	// TODO: delete after datastore transformation complete
	private Entity classTransformation(SpinnerClass spinnerClass, int retryCounter) throws Exception {
		Transaction txn = datastore.newTransaction();
		Entity scEntity = getClassByID(txn, spinnerClass.getId());
		if (scEntity == null) {
			try {
				Key classKey = datastore.allocateId(classFactory.newKey());
				scEntity = Entity.newBuilder(classKey).set("classId", spinnerClass.getId()).set("className", spinnerClass.getSpinnerClassName()).set("hyperLink", spinnerClass.getHyperLink()).set("lockForRegistration", spinnerClass.getLockForRegistration()).set("openForRegistrationMode", spinnerClass.getOpenForRegistrationMode()).build();
				txn.add(scEntity);
				txn.commit();
			} catch (com.google.cloud.datastore.DatastoreException e) {
				if (retryCounter < datastoreTxnRetries) {
					retryCounter = retryCounter + 1;
					return classTransformation(spinnerClass, retryCounter);
				} else {
					throw e;
				}
			} finally

			{
				if (txn.isActive()) {
					txn.rollback();
				}
			}
		}
		return scEntity;
	}

}
