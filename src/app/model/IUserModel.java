package app.model;

public interface IUserModel {

    enum ComparisonRating{
        Good(30), Average(10), Bad(0);

        private int _experience;

        private ComparisonRating(int experience) {
            _experience = experience;
        }

        public int getExperience(){
            return _experience;
        }
    }

    void updateUserXP(ComparisonRating rating);

    int getDailyStreak();

    void addListener(UserModelListener listener);
}
