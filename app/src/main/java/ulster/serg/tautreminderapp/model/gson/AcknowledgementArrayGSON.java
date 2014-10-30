package ulster.serg.tautreminderapp.model.gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philliphartin on 01/10/2014.
 */
public class AcknowledgementArrayGSON {

    private ArrayList<AcknowledgementGSON> acknowledgements;

    public List<AcknowledgementGSON> getAcknowledgements() {
        return acknowledgements;
    }

    public void setAcknowledgements(ArrayList<AcknowledgementGSON> acknowledgements) {
        this.acknowledgements = acknowledgements;
    }

}
