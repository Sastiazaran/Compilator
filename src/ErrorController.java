import java.util.Vector;

public class ErrorController {
    private String name;
    private Vector<String> errors;

    public ErrorController(String name){
        setName(name);
        setErrors();
    }

    public void storeError(String err){
        getErrors().add(err);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Vector<String> getErrors(){
        return errors;
    }

    public void setErrors(){
        this.errors = new Vector<String>();
    }
}
