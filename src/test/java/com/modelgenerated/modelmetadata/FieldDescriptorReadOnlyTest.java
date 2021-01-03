/* FieldDescriptorReadOnlyTest.java
*
*/

package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.modelmetadata.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class FieldDescriptorReadOnlyTest extends TestCase {

    public FieldDescriptorReadOnlyTest(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(FieldDescriptorReadOnlyTest.class);
        return suite;
    }

    
    /** 
     * Chain together two objects in a read only join.
     * 
     * Here is an example. 
     * -A model has three object: Customer, Contact, and Transaction. 
     * -The Customer has a reference to a primary Contact record. 
     * -A transaction has a reference to a 
     * 
     * 
     */
    public void testReadOnlyJoinChainParentParent() {
        Model model = new Model();

        String CLASS_CONTACT = "com.mg.Contact";
        String CLASS_CUSTOMER = "com.mg.Customer";
        String CLASS_TRANSACTION = "com.mg.Transaction";
        
        String TABLE_CONTACT = "Contact";
        String TABLE_CUSTOMER = "Customer";
        String TABLE_TRANSACTION = "Transaction";
        
        String FIELD_CONTACT_NAME = "Name";
        String FIELD_CUSTOMER_PRIMARYCONTACT = "PrimaryContact";
        String FIELD_TRANSACTION_CUSTOMERPRIMARYCONTACT = "CustomerPrimaryContact";
        String FIELD_TRANSACTION_CUSTOMERPRIMARYCONTACTNAME = "CustomerPrimaryContactName";

        // build Contact
        ObjectDescriptor contactObjectDescriptor = new ObjectDescriptor(model);
        contactObjectDescriptor.setValueObjectInterface(new ClassDescriptor(CLASS_CONTACT));
        contactObjectDescriptor.setTableName(TABLE_CONTACT);
        model.addObject(contactObjectDescriptor);
        
        FieldDescriptor contactNameFieldDescriptor = new FieldDescriptor(contactObjectDescriptor);
        contactNameFieldDescriptor.setName(FIELD_CONTACT_NAME);
        contactNameFieldDescriptor.setType(FieldTypeEnum.STRING);
        contactObjectDescriptor.addField(contactNameFieldDescriptor);
        
        // build Customer 
        ObjectDescriptor customerObjectDescriptor = new ObjectDescriptor(model);
        customerObjectDescriptor.setValueObjectInterface(new ClassDescriptor(CLASS_CUSTOMER));
        customerObjectDescriptor.setTableName(TABLE_CUSTOMER);
        model.addObject(customerObjectDescriptor);
        
        FieldDescriptor customerContactFieldDescriptor = new FieldDescriptor(customerObjectDescriptor);
        customerContactFieldDescriptor.setName(FIELD_CUSTOMER_PRIMARYCONTACT);
        customerContactFieldDescriptor.setType(FieldTypeEnum.CLASS);
        customerContactFieldDescriptor.setClassDescriptor(new ClassDescriptor(CLASS_CONTACT));
        customerObjectDescriptor.addField(customerContactFieldDescriptor);
        
        
        // Build Transaction
        ObjectDescriptor transactionObjectDescriptor = new ObjectDescriptor(model);
        transactionObjectDescriptor.setValueObjectInterface(new ClassDescriptor(CLASS_TRANSACTION));
        transactionObjectDescriptor.setTableName(TABLE_TRANSACTION);
        model.addObject(transactionObjectDescriptor);
        
        FieldDescriptor objectThreeFieldOne = new FieldDescriptor(transactionObjectDescriptor);
        objectThreeFieldOne.setName(FIELD_TRANSACTION_CUSTOMERPRIMARYCONTACT);
        objectThreeFieldOne.setType(FieldTypeEnum.CLASS);
        objectThreeFieldOne.setClassDescriptor(new ClassDescriptor("com.mg.Customer"));
        transactionObjectDescriptor.addField(objectThreeFieldOne);
        
        
        FieldDescriptor objectThreeFieldTwo = new FieldDescriptor(transactionObjectDescriptor);
        objectThreeFieldTwo.setName(FIELD_TRANSACTION_CUSTOMERPRIMARYCONTACTNAME);
        objectThreeFieldTwo.setType(FieldTypeEnum.READONLYJOIN);
        objectThreeFieldTwo.setJoinField(FIELD_TRANSACTION_CUSTOMERPRIMARYCONTACT);
        objectThreeFieldTwo.setColumnName(FIELD_TRANSACTION_CUSTOMERPRIMARYCONTACT + "." + FIELD_CUSTOMER_PRIMARYCONTACT + "." + FIELD_CONTACT_NAME);
        objectThreeFieldTwo.setType(FieldTypeEnum.READONLYJOIN);
        transactionObjectDescriptor.addField(objectThreeFieldTwo);
        
        FieldDescriptor joinedFieldDescriptor = objectThreeFieldTwo.getJoinedFieldDescriptor();
        Logger.debug(this, "joinedFieldDescriptor");
        Logger.debug(this, joinedFieldDescriptor.display());
        assertTrue("joinedFieldDescriptor.equals(contactNameFieldDescriptor)", joinedFieldDescriptor.equals(contactNameFieldDescriptor));
        
        assertTrue("joinedFieldDescriptor.equals(contactNameFieldDescriptor)", joinedFieldDescriptor.equals(contactNameFieldDescriptor));
        
        
        
        // this is going away
        FieldDescriptor joinedFieldAlias = objectThreeFieldTwo.getJoinedFieldAlias();
        Logger.debug(this, "joinedFieldAlias");
        Logger.debug(this, joinedFieldAlias.display());

        
        //assertTrue("id should equal id", account3.getId().equals(id));
        //assertTrue("userName should equal userName2", account3.getUserName().equals(userName2));
        //assertTrue("password should equal password2", account3.getPassword().equals(password2));

    }

    /** 
     * Should be able to create readonly joins to objects that are external to this model as long as we have 
     * the info  needed to create the join. 
     */
    public void xtestReadOnlyJoinToExternalObject() {
        Model model = new Model();

        ObjectDescriptor objectOne = new ObjectDescriptor(model);
        objectOne.setValueObjectInterface(new ClassDescriptor("com.mg.customer"));
        model.addObject(objectOne);
        
        FieldDescriptor fieldOne = new FieldDescriptor(objectOne);
        fieldOne.setName("FieldOne");
        fieldOne.setType(FieldTypeEnum.CLASS);
        fieldOne.setClassDescriptor(new ClassDescriptor("com.modelgenerated.Customer"));
        objectOne.addField(fieldOne);
        
        
        FieldDescriptor fieldTwo = new FieldDescriptor(objectOne);
        fieldTwo.setName("FieldTwo");
        fieldOne.setType(FieldTypeEnum.READONLYJOIN);
       
        
        objectOne.addField(fieldOne);
        

        //assertTrue("id should equal id", account3.getId().equals(id));
        //assertTrue("userName should equal userName2", account3.getUserName().equals(userName2));
        //assertTrue("password should equal password2", account3.getPassword().equals(password2));

    }


}
