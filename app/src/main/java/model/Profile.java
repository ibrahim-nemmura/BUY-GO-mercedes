package model;

public class Profile {


    private String profileName;
    private String profileCollege;
    private String profileAddress;
    private int profileAge;
    private String profilePincode;
    private String profileGender;
    private String profilePic;
    private String profileContact;
    private String profileUpi;
    private String profileEmail;

    public Profile() {
    }

    public Profile(String profileName, String profileCollege, String profileAddress, int profileAge, String profilePincode, String profileGender, String profilePic, String profileContact,String profileUpi,String profileEmail) {
        this.profileName = profileName;
        this.profileCollege = profileCollege;
        this.profileAddress = profileAddress;
        this.profileAge = profileAge;
        this.profilePincode = profilePincode;
        this.profileGender = profileGender;
        this.profilePic = profilePic;
        this.profileContact = profileContact;
        this.profileUpi=profileUpi;
        this.profileEmail=profileEmail;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    public String getProfileUpi() {
        return profileUpi;
    }

    public void setProfileUpi(String profileUpi) {
        this.profileUpi = profileUpi;
    }

    public String getProfileContact() {
        return profileContact;
    }

    public void setProfileContact(String profileContact) {
        this.profileContact = profileContact;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileCollege() {
        return profileCollege;
    }

    public void setProfileCollege(String profileCollege) {
        this.profileCollege = profileCollege;
    }

    public String getProfileAddress() {
        return profileAddress;
    }

    public void setProfileAddress(String profileAddress) {
        this.profileAddress = profileAddress;
    }

    public int getProfileAge() {
        return profileAge;
    }

    public void setProfileAge(int profileAge) {
        this.profileAge = profileAge;
    }

    public String getProfilePincode() {
        return profilePincode;
    }

    public void setProfilePincode(String profilePincode) {
        this.profilePincode = profilePincode;
    }

    public String getProfileGender() {
        return profileGender;
    }

    public void setProfileGender(String profileGender) {
        this.profileGender = profileGender;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
