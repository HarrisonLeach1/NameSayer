package app.model;

/**
 * An IUserModel represents a model that stores information about the users
 * daily streak progress and level progress.
 *
 * Having the IDataModel interface allows for dependency injection, such that
 * the users are dependent on an abstraction rather than a concrete
 * implementation. This allows for increased flexibility if the database
 * implementation is changed.
 */
public interface IUserModel {

    /**
     * A ComparisonRating represents the possible ratings that a user can
     * receive for their recording. Each rating has experience points
     * associated with it.
     */
    enum ComparisonRating{
        Good(30), Average(10), Bad(0);

        private int _experience;

        ComparisonRating(int experience) {
            _experience = experience;
        }

        public int getExperience(){
            return _experience;
        }
    }

    /**
     * Updates and saves the experience points by the amount associated
     * with the given rating.
     * @param rating
     */
    void updateUserXP(ComparisonRating rating);

    /**
     * Adds a listener to this IUserModel object which is updated whenever
     * the users experience level changes.
     * @param listener
     */
    void addListener(UserModelListener listener);

    /**
     * Returns the number of days in a row the user has logged in to the
     * application.
     * @return the current daily streak of the user
     */
    int getDailyStreak();

    /**
     * Returns the users current level
     */
    int getUserLevel();
}
