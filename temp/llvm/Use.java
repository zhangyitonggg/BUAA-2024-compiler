package llvm;

import llvm.value.User;
import llvm.value.Value;

public class Use {
    private final User user;
    private final Value used;
    
    public Use(User user, Value used) {
        this.user = user;
        this.used = used;
    }
    
    public User getUser() {
        return user;
    }
}
