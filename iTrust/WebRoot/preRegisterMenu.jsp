<%--
  Panel for preregistering a patient on the iTrust landing page.
  (UC 91)
--%>

<%@page import="org.apache.commons.lang.StringEscapeUtils" %>

<%
    String emailError = "";
    if (!preRegisterAction.isValidEmail()) {
        emailError = "The email you entered was invalid.";
    } else {
        if (!preRegisterAction.isNewEmail()) {
            emailError = "The email you entered is already in use.";
        }
    }

    String passwordMatchError = "";
    if (!preRegisterAction.doPasswordsMatch()) {
        passwordMatchError = "The passwords do not match.";
    }

    String phoneError = "";
    if (!preRegisterAction.isValidPhone()) {
        phoneError = "The phone number you entered was invalid.";
    }

    String icPhoneError = "";
    if (!preRegisterAction.isValidIcPhone()) {
        icPhoneError = "The phone number you entered was invalid.";
    }

%>

<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">Pre-register</h2>
    </div>
    <div class="panel-body">
        <form method="post" action="/iTrust/preRegister.jsp">
            <h4>Personal Information</h4>
            First name <font color="red">*</font><br/>
            <input type="text" maxlength="20" id="pre_firstname" name="pre_firstname" style="width: 97%;" value=""
                   required>
            <br/><br/>

            Last name <font color="red">*</font><br/>
            <input type="text" maxlength="20" id="pre_lastname" name="pre_lastname" style="width: 97%;" value=""
                   required>
            <br/><br/>

            <div style="align: center; margin-bottom: 10px;">
                <span class="iTrustError"
                      style="font-size: 16px;"><%= StringEscapeUtils.escapeHtml("" + (emailError)) %></span>
            </div>

            Email <font color="red">*</font><br/>
            <input type="text" maxlength="55" id="pre_email" name="pre_email" style="width: 97%;" value="" required>
            <br/><br/>

            <div style="align: center; margin-bottom: 10px;">
                <span class="iTrustError"
                      style="font-size: 16px;"><%= StringEscapeUtils.escapeHtml("" + (passwordMatchError)) %></span>
            </div>

            Password <font color="red">*</font><br/>
            <input type="password" maxlength="20" id="pre_password" name="pre_password" style="width: 97%;" required>
            <br/><br/>

            Verify password <font color="red">*</font><br/>
            <input type="password" maxlength="20" id="pre_verify" name="pre_verify" style="width: 97%;" required>
            <br/><br/>

            Address 1 <br/>
            <input type="text" maxlength="20" id="pre_address_1" name="pre_address_1" style="width: 97%;">
            <br/><br/>

            Address 2 <br/>
            <input type="text" maxlength="20" id="pre_address_2" name="pre_address_2" style="width: 97%;">
            <br/><br/>

            City <br/>
            <input type="text" maxlength="15" id="pre_city" name="pre_city" style="width: 97%;">
            <br/><br/>

            State <br/>
            <select id="pre_state" name="pre_state" style="width: 97%;">
                <option value="" selected="selected">Select a State</option>
                <option value="AL">Alabama</option>
                <option value="AK">Alaska</option>
                <option value="AZ">Arizona</option>
                <option value="AR">Arkansas</option>
                <option value="CA">California</option>
                <option value="CO">Colorado</option>
                <option value="CT">Connecticut</option>
                <option value="DE">Delaware</option>
                <option value="DC">District Of Columbia</option>
                <option value="FL">Florida</option>
                <option value="GA">Georgia</option>
                <option value="HI">Hawaii</option>
                <option value="ID">Idaho</option>
                <option value="IL">Illinois</option>
                <option value="IN">Indiana</option>
                <option value="IA">Iowa</option>
                <option value="KS">Kansas</option>
                <option value="KY">Kentucky</option>
                <option value="LA">Louisiana</option>
                <option value="ME">Maine</option>
                <option value="MD">Maryland</option>
                <option value="MA">Massachusetts</option>
                <option value="MI">Michigan</option>
                <option value="MN">Minnesota</option>
                <option value="MS">Mississippi</option>
                <option value="MO">Missouri</option>
                <option value="MT">Montana</option>
                <option value="NE">Nebraska</option>
                <option value="NV">Nevada</option>
                <option value="NH">New Hampshire</option>
                <option value="NJ">New Jersey</option>
                <option value="NM">New Mexico</option>
                <option value="NY">New York</option>
                <option value="NC">North Carolina</option>
                <option value="ND">North Dakota</option>
                <option value="OH">Ohio</option>
                <option value="OK">Oklahoma</option>
                <option value="OR">Oregon</option>
                <option value="PA">Pennsylvania</option>
                <option value="RI">Rhode Island</option>
                <option value="SC">South Carolina</option>
                <option value="SD">South Dakota</option>
                <option value="TN">Tennessee</option>
                <option value="TX">Texas</option>
                <option value="UT">Utah</option>
                <option value="VT">Vermont</option>
                <option value="VA">Virginia</option>
                <option value="WA">Washington</option>
                <option value="WV">West Virginia</option>
                <option value="WI">Wisconsin</option>
                <option value="WY">Wyoming</option>
            </select>
            <br/><br/>

            Zip <br/>
            <input type="text" maxlength="10" id="pre_zip" name="pre_zip" style="width: 97%;">
            <br/><br/>

            <div style="align: center; margin-bottom: 10px;">
                <span class="iTrustError"
                      style="font-size: 16px;"><%= StringEscapeUtils.escapeHtml("" + (phoneError)) %></span>
            </div>
            Phone (XXX-XXX-XXXX)<br/>
            <input type="text" maxlength="20" id="pre_phone" name="pre_phone" style="width: 97%;">
            <br/><br/>

            Height (in inches) <br/>
            <input type="number" step=any id="pre_height" name="pre_height" style="width: 97%;">
            <br/><br/>

            Weight (in pounds) <br/>
            <input type="number" step=any id="pre_weight" name="pre_weight" style="width: 97%;">
            <br/><br/>

            I'm a smoker: <input type="checkbox" maxlength="20" id="pre_smoker" name="pre_smoker">
            <br/><br/>

            <br/>
            <h4>Insurance Information</h4>
            Provider Name <br/>
            <input type="text" maxlength="20" id="insurance_name" name="insurance_name" style="width: 97%;">
            <br/><br/>

            Address 1 <br/>
            <input type="text" maxlength="20" id="insurance_address_1" name="insurance_address_1" style="width: 97%;">
            <br/><br/>

            Address 2 <br/>
            <input type="text" maxlength="20" id="insurance_address_2" name="insurance_address_2" style="width: 97%;">
            <br/><br/>

            City <br/>
            <input type="text" maxlength="15" id="insurance_city" name="insurance_city" style="width: 97%;">
            <br/><br/>

            State <br/>
            <select id="insurance_state" name="insurance_state" style="width: 97%;">
                <option value="" selected="selected">Select a State</option>
                <option value="AL">Alabama</option>
                <option value="AK">Alaska</option>
                <option value="AZ">Arizona</option>
                <option value="AR">Arkansas</option>
                <option value="CA">California</option>
                <option value="CO">Colorado</option>
                <option value="CT">Connecticut</option>
                <option value="DE">Delaware</option>
                <option value="DC">District Of Columbia</option>
                <option value="FL">Florida</option>
                <option value="GA">Georgia</option>
                <option value="HI">Hawaii</option>
                <option value="ID">Idaho</option>
                <option value="IL">Illinois</option>
                <option value="IN">Indiana</option>
                <option value="IA">Iowa</option>
                <option value="KS">Kansas</option>
                <option value="KY">Kentucky</option>
                <option value="LA">Louisiana</option>
                <option value="ME">Maine</option>
                <option value="MD">Maryland</option>
                <option value="MA">Massachusetts</option>
                <option value="MI">Michigan</option>
                <option value="MN">Minnesota</option>
                <option value="MS">Mississippi</option>
                <option value="MO">Missouri</option>
                <option value="MT">Montana</option>
                <option value="NE">Nebraska</option>
                <option value="NV">Nevada</option>
                <option value="NH">New Hampshire</option>
                <option value="NJ">New Jersey</option>
                <option value="NM">New Mexico</option>
                <option value="NY">New York</option>
                <option value="NC">North Carolina</option>
                <option value="ND">North Dakota</option>
                <option value="OH">Ohio</option>
                <option value="OK">Oklahoma</option>
                <option value="OR">Oregon</option>
                <option value="PA">Pennsylvania</option>
                <option value="RI">Rhode Island</option>
                <option value="SC">South Carolina</option>
                <option value="SD">South Dakota</option>
                <option value="TN">Tennessee</option>
                <option value="TX">Texas</option>
                <option value="UT">Utah</option>
                <option value="VT">Vermont</option>
                <option value="VA">Virginia</option>
                <option value="WA">Washington</option>
                <option value="WV">West Virginia</option>
                <option value="WI">Wisconsin</option>
                <option value="WY">Wyoming</option>
            </select>
            <br/><br/>

            Zip <br/>
            <input type="text" maxlength="10" id="insurance_zip" name="insurance_zip" style="width: 97%;">
            <br/><br/>

            <div style="align: center; margin-bottom: 10px;">
                <span class="iTrustError"
                      style="font-size: 16px;"><%= StringEscapeUtils.escapeHtml("" + (icPhoneError)) %></span>
            </div>

            Phone (XXX-XXX-XXXX)<br/>
            <input type="text" maxlength="20" id="insurance_phone" name="insurance_phone" style="width: 97%;">
            <br/><br/>

            <input type="submit" value="Register">
        </form>
    </div>

</div>
