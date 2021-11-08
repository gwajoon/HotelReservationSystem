package util.exception;



public class RegisteredGuestEmailExistException extends Exception
{
    public RegisteredGuestEmailExistException()
    {
    }
    
    
    
    public RegisteredGuestEmailExistException(String msg)
    {
        super(msg);
    }
}