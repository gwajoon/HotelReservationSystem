package util.exception;



public class EmployeeEmailExistException extends Exception
{
    public EmployeeEmailExistException()
    {
    }
    
    
    
    public EmployeeEmailExistException(String msg)
    {
        super(msg);
    }
}