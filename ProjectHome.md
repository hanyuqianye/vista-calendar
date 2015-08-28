Java Swing Vista like calendar


Example to use
```
package com;

import com.common.vistacalendar.AnimatedDateTime;
import com.common.vistacalendar.DateExt;
import com.common.vistacalendar.internal.DateSelectionAction;
import javax.swing.JDialog;

public class Main {

     public static void main(String[] args) {
        boolean includeTime = true;
        JDialog dialog = AnimatedDateTime.createDialog(null, includeTime, new DateSelectionAction() {

            @Override
            public void dateSelected(DateExt date) {
                
            }
        });
        dialog.setVisible(true);
        
    }
}
```

Images

![https://vista-calendar.googlecode.com/svn/wiki/1.png](https://vista-calendar.googlecode.com/svn/wiki/1.png)
![https://vista-calendar.googlecode.com/svn/wiki/2.png](https://vista-calendar.googlecode.com/svn/wiki/2.png)
![https://vista-calendar.googlecode.com/svn/wiki/3.png](https://vista-calendar.googlecode.com/svn/wiki/3.png)