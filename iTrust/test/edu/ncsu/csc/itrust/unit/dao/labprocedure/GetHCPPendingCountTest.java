package edu.ncsu.csc.itrust.unit.dao.labprocedure;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.LabProcedureBean;
import edu.ncsu.csc.itrust.dao.mysql.LabProcedureDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;


public class GetHCPPendingCountTest extends TestCase {
    private LabProcedureDAO lpDAO = TestDAOFactory.getTestInstance().getLabProcedureDAO();

    private TestDataGenerator gen;
    private LabProcedureBean l1;

    @Override
    protected void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.labProcedures();
        // first procedure
        l1 = new LabProcedureBean();
        l1.setPid(3L);
        l1.setOvID(918L);
        l1.setLoinc("10763-1");
        l1.statusPending();
        l1.setCommentary("");

    }

    public void testGetHCPPendingCountTest() throws Exception {
        int count = lpDAO.getHCPPendingCount(9000000000L);
        assertEquals(0, count);
        lpDAO.addLabProcedure(l1);
        count = lpDAO.getHCPPendingCount(9000000000L);
        assertEquals(1, count);
    }

}
