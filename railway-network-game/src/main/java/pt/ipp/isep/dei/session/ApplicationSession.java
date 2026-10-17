package pt.ipp.isep.dei.session;

public class ApplicationSession {
    private static ApplicationSession instance;
    private UserSession currentSession;

    private ApplicationSession() {
        this.currentSession = new UserSession();
    }

    public static ApplicationSession getInstance() {
        if (instance == null) {
            instance = new ApplicationSession();
        }
        return instance;
    }

    public UserSession getCurrentSession() {
        return currentSession;
    }

    public void updateBudget(double amount) {
    }
}
