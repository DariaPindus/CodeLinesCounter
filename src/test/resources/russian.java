/* Мне нужно оставить комментарии
на русском языке
:))
 */
public class Example {

    public void print() {
        System.out.println(" И тут еще /* комментариев");
    }

    public void continuePrint(/* peremennaya */ int a ) {
        String res = " // не должно учиваться";
        res += "*////*конец";
        // /*
        /*
         commenty
         */
    }
}