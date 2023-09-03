package com.gre.prep.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.Models.WordModel;
import com.gre.prep.Models.WordModel2;
import com.gre.prep.databinding.ActivityWordLoadBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class WordLoadActivity extends AppCompatActivity {
    ActivityWordLoadBinding binding;
    HashSet<String> originalWordSet;
    List<WordModel2> originalWordList;
    HashMap<String, HashMap<String, Object>> originalWordMap;
    HashMap<String, HashMap<String, Object>> newWordDbMap;
    DBRefManager dbRefManager;
    CustomProgressDialog progressDialog;
    StringBuilder stringBuilder;
    HashMap<Character, String> groupMap;
    String cat;
    HashMap<String, String> newWordsMap, oldWordRevisionMap;
    String[] newWordsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordLoadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbRefManager = new DBRefManager();
        progressDialog = new CustomProgressDialog(WordLoadActivity.this);
        progressDialog.show();
        originalWordSet = new HashSet<>();
        originalWordList = new ArrayList<>();
        groupMap = new HashMap<>();
        newWordsMap = new HashMap<>();
        originalWordMap = new HashMap<>();
        newWordDbMap = new HashMap<>();
        oldWordRevisionMap = new HashMap<>();
        stringBuilder = new StringBuilder();

        newWordsArr = new String[]{"aberrant", "aberration", "abstain", "abstruse", "accolade", "acerbic", "acrimony", "adamant", "admonish", "admonitory", "aesthete", "aesthetic", "amalgam", "ambiguous", "ambivalent", "ameliorate", "amenable", "amorphous", "anomalous", "anomaly", "antipathy", "antithetical", "apathetic", "apathy", "apocryphal", "appease", "arbitrary", "arcane", "arduous", "artful", "ascetic", "askance", "audacious", "audacity", "auspicious", "austere", "avaricious", "banal", "banality", "belie", "belligerent", "betray", "blatant", "bolster", "brazen", "bucolic", "bumbling", "burgeon", "calumny", "capricious", "castigate", "censure", "chastise", "chortle", "circumscribe", "circumvent", "commensurate", "concede", "conflate", "confound", "conspicuous", "constituent", "construe", "contingent", "contrition", "contrive", "copious", "craven", "cryptic", "culminate", "culpability", "decorous", "decorum", "deferential", "deleterious", "delineate", "demur", "denigrate", "denote", "derivative", "derive", "dictatorial", "didactic", "diffident", "discrete", "disinterested", "dispassionate", "disseminate", "dogmatic", "duress", "eclectic", "economical", "edifying", "efficacious", "egregious", "elicit", "elucidate", "eminent", "enervate", "engender", "entrenched", "ephemeral", "equivocal", "eradicate", "erudite", "eschew", "esoteric", "espouse", "exacerbate", "exacting", "exalt", "exonerate", "expound", "extant", "fallacious", "fastidious", "flux", "foment", "forlorn", "forthcoming", "forthright", "fortuitous", "frivolous", "frugal", "frustrate", "furtive", "gainsay", "gall", "galvanize", "garrulous", "gauche", "germane", "glib", "glut", "gossamer", "grandiloquent", "gratuitous", "gregarious", "guileless", "hackneyed", "haphazard", "harangue", "harried", "haughty", "heinous", "hegemony", "heretic", "iconoclast", "iconoclastic", "idiosyncrasy", "ignoble", "ignominious", "immutable", "impartial", "impertinent", "implacable", "implausible", "imprudent", "impudent", "incisive", "incongruous", "incorrigible", "indecorous", "indifferent", "inexorable", "ingenuous", "ingratiate", "inimical", "innocuous", "inscrutable", "insidious", "insolent", "intangible", "intrepid", "inveterate", "involved", "irrevocable", "itinerant", "jingoism", "jovial", "jubilant", "juxtapose", "laconic", "lambast", "languid", "largess", "laudable", "lionize", "magnanimous", "maintain", "maladroit", "maverick", "mawkish", "mendacity", "mercurial", "meticulous", "misconstrue", "mitigate", "mitigating", "mollify", "mundane", "myriad", "negligible", "nonplussed", "nuance", "obscure", "opulence", "ostentatious", "ostracize", "panache", "parochial", "parsimonious", "pedantic", "pedestrian", "pejorative", "perfidy", "pernicious", "petulant", "placate", "platitude", "poignant", "polemic", "posit", "pragmatic", "precipitous", "preclude", "precocious", "predilection", "prescience", "prevaricate", "prodigal", "prodigious", "profligate", "prolific", "propitious", "provincial", "pundit", "qualify", "querulous", "quotidian", "ravenous", "rebuke", "reconcile", "recondite", "refractory", "refute", "reproach", "repudiate", "rescind", "restive", "resurgent", "reticent", "reverent", "rudimentary", "rustic", "sanction", "sanction", "scrupulous", "scrupulous", "scrupulous", "soporific", "specious", "specious", "sporadic", "spurious", "spurious", "stringent", "subsume", "subsume", "subversive", "sullen", "superfluous", "superfluous", "supplant", "sycophant", "taciturn", "tantamount", "temperance", "tempered", "tenacious", "timorous", "torpor", "tortuous", "tractable", "transient", "travesty", "treacherous", "treacherous", "trite", "truncate", "undermine", "underscore", "unequivocal", "unscrupulous", "upbraid", "vacillate", "vehement", "venality", "venerate", "veracious", "vilify", "vindicate", "vociferous", "volubility", "wanting", "winsome", "aboveboard", "abysmal", "acme", "advocate", "advocate", "affable", "affluent", "altruism", "amiable", "amply", "amuck", "analogous", "animosity", "antedated", "antiquated", "apex", "aphorism", "aphoristic", "appreciable", "apprehension", "archaic", "ascendancy", "ascribe", "assail", "assuage", "augment", "autonomously", "avarice", "avert", "avid", "badger", "balk", "banish", "beatific", "becoming", "begrudge", "begrudge", "behooves", "belittle", "bellicose", "benign", "benign", "besiege", "besmirch", "bleak", "boon", "boon", "boorish", "brusquely", "buck", "buttress", "cadaverous", "candid", "candidness", "cardinal", "censor", "cerebral", "champion", "chauvinist", "check", "check", "checkered", "chivalrous", "clemency", "coalesce", "cogent", "cohesive", "collusion", "colossal", "commendable", "complacent", "complementary", "compound", "compound", "conducive", "conniving", "consecrate", "constraint", "consummate", "consummate", "convivial", "convoluted", "cornucopia", "corroborate", "cosmopolitan", "credence", "creditable", "credulity", "cumbersome", "dearth", "debase", "debunk", "decimation", "degrade", "delegate", "deliberate", "deliberate", "demean", "demure", "deride", "derisive", "derogative", "desecrate", "destitute", "destitute", "deter", "deter", "detrimental", "devolve", "devolve", "diabolical", "differentiate", "differentiate", "dilapidated", "diligent", "discord", "discreet", "discriminate", "disenfranchise", "disheartened", "disparate", "dispatch", "dispatch", "docile", "dog", "dog", "dupe", "dupe", "eccentric", "egotist", "eke", "elaborate", "elaborate", "elude", "elusive", "embellish", "embroiled", "empathetic", "emulate", "endemic", "enmity", "entice", "enumerate", "enumerate", "err", "errant", "erratic", "euphoria", "evasive", "evasive", "evenhanded", "exasperate", "excruciating", "exemplify", "exemplify", "exhort", "extenuating", "facetious", "fawn", "ferret", "fete", "fickle", "finagle", "fledgling", "fleece", "flounder", "flush", "foible", "foolhardy", "forthright", "futile", "genial", "genteel", "glean", "glib", "goad", "grovel", "guffaw", "hamper", "hamstrung", "heyday", "hodgepodge", "hound", "humdrum", "illicit", "immaterial", "impeccable", "impede", "impending", "impermeable", "implicate", "implicate", "imponderable", "impregnable", "inadvertent", "inarticulate", "incessant", "inclement", "inclement", "incumbent", "indict", "indigenous", "indignant", "industrious", "inflammable", "ingenuity", "inkling", "insipid", "insolvent", "intermittent", "inundate", "irascible", "irk", "irresolute", "jargon", "jocular", "junta", "laborious", "leery", "lethargic", "lucid", "macabre", "malady", "malevolent", "malleable", "malodorous", "martial", "maxim", "meander", "melancholy", "melee", "mesmerize", "misanthrope", "miscreant", "miser", "misogynist", "moment", "moot", "morose", "morph", "muted", "obdurate", "obliging", "obstinate", "ornate", "paradoxical", "pastoral", "patronize", "paucity", "peevish", "perennial", "perpetuate", "perquisite", "pertinent", "perturb", "peruse", "pine", "pinnacle", "piquant", "pithy", "pittance", "placid", "plodding", "ploy", "powwow", "precarious", "precedent", "preempt", "preemptive", "presumption", "presumptuous", "prevail", "pristine", "profuse", "proponent", "provisional", "pugnacious", "qualm", "quandary", "quip", "raffish", "raft", "rakish", "rankle", "rash", "redress", "relegate", "remiss", "renege", "replete", "reprobate", "reservation", "resignation", "resolve", "respite", "retiring", "retract", "rile", "robust", "sanctimonious", "sanguine", "slapdash", "smattering", "smug", "snide", "snub", "sordid", "spendthrift", "spurn", "squander", "staid", "steadfast", "stem", "stipend", "stolid", "stymie", "summit", "surly", "tact", "tarnish", "tawdry", "taxing", "telling", "telltale", "tender", "thoroughgoing", "thrifty", "thwart", "tirade", "tout", "transitory", "travail", "tribulation", "tumult", "uncanny", "uncompromising", "unconscionable", "underwrite", "unnerve", "unprecedented", "unruly", "unseemly", "urbane", "vacuous", "vanquish", "variance", "veneer", "vicarious", "vie", "vindictive", "virago", "voracious", "wanton", "wax", "whimsical", "zenith", "abjure", "abrogate", "adjudicate", "afford", "alacrity", "anachronism", "anathema", "anemic", "anodyne", "antic", "aplomb", "apogee", "apostate", "apothegm", "apotheosis", "approbatory", "appropriate", "appurtenant", "arch", "arrant", "arriviste", "arrogate", "artifice", "artless", "asperity", "assiduously", "atavism", "attenuate", "autocratic", "baleful", "base", "bastardization", "beg", "bemoan", "benighted", "bereft", "besotted", "bilious", "blinkered", "bowdlerize", "bridle", "bristle", "broadside", "bromide", "brook", "browbeat", "byzantine", "callow", "canard", "capitulate", "cataclysm", "catholic", "chimera", "choleric", "churlish", "complaisant", "complicit", "conciliate", "concomitant", "conflagration", "conflate", "contentious", "corollary", "cosseted", "coterminous", "countermand", "cow", "crestfallen", "crystallize", "cupid", "debonair", "decry", "defray", "deign", "demonstative", "denouement", "derelict", "desiccated", "desideratum", "despot", "diatribe", "diminutive", "disabuse", "dissemble", "dissipate", "dissolution", "doleful", "dolorous", "doughty", "dovetail", "duplicity", "ebullient", "effervescent", "effrontery", "elegiac", "embroyonic", "empiricism", "enamor", "encumber", "enjoin", "enormity", "enthrall", "epigram", "epiphany", "eponym", "equitable", "equivocate", "ersatz", "estimable", "ethereal", "evanescent", "excoriate", "execrate", "exegesis", "exemplar", "exiguity", "exorbitant", "expansive", "expunge", "expurgate", "extrapolate", "facile", "factious", "factitious", "feckless", "fecund", "fell", "firebrand", "flag", "flippant", "flummox", "fractious", "gaffe", "gambit", "gerrymander", "graft", "grandiloquent", "gumption", "hagiographic", "hail", "halcyon", "hauteur", "hector", "hedge", "histrionic", "hoary", "hobble", "hoodwink", "hubris", "illustrious", "imbibe", "imbroglio", "immure", "impecunious", "imperious", "impervious", "impetuous", "importuned", "improvident", "impugn", "impute", "inanity", "inchoate", "incontrovertible", "indigent", "ineffable", "ineluctable", "inequity", "infelicitous", "inimitable", "insouciance", "insufferable", "internecine", "intimation", "inure", "invective", "invidious", "inviolable", "inviolate", "irrefutable", "jaundice", "jejune", "jingoist", "juggernaut", "kowtow", "lacerate", "lachrymose", "lampoon", "languish", "lascivious", "limpid", "litany", "loath", "lugubrious", "machinate", "magisterial", "malapropism", "malfeasance", "malingerer", "martinet", "maudlin", "maunder", "mellifluous", "mendicant", "meteoric", "mettlesome", "modicum", "mordant", "moribund", "mulct", "nadir", "nettlesome", "noisome", "nonchalant", "objurgate", "oblique", "obstreperous", "obtain", "obtuse", "officious", "ossify", "overweening", "palatable", "palaver", "palimpsest", "panacea", "panegyric", "paragon", "pariah", "parvenu", "patent", "pecuniary", "pellucid", "penurious", "percipient", "peremptory", "perfunctory", "peripatetic", "perspicacious", "phantasmagorical", "philistine", "phlegmatic", "picayune", "pillory", "pith", "plucky", "Pollyannaish", "ponderous", "pontificate", "portentous", "precipitate", "presentiment", "primacy", "probity", "prognostication", "prolixity", "promulgate", "propitiate", "prosaic", "proscribe", "proselytize", "protean", "provident", "puerile", "puissant", "punctilious", "pyrrhic", "quail", "quisling", "quixotic", "raconteur", "raillery", "rapprochement", "rarefied", "recapitulation", "recrimination", "recrudesce", "redoubtable", "remonstrate", "reprisal", "ribald", "row", "sagacious", "sangfroid", "sardonic", "sartorial", "saturnine", "schadenfreude", "sedulous", "self-effacing", "semblance", "sententious", "simulacrum", "sinecure", "solecism", "solicitous", "solicitude", "spartan", "splenetic", "squelch", "stalwart", "stultify", "subterfuge", "supercilious", "sybarite", "temerity", "tempestuous", "tendentious", "transmute", "trenchant", "truculence", "truculent", "turgid", "turpitude", "tyro", "umbrage", "unassailable", "unflappable", "unforthcoming", "unimpeachable", "unprepossessing", "unpropitious", "unstinting", "untenable", "untoward", "untrammeled", "unviable", "vaunted", "venial", "verisimilitude", "veritable", "vicissitude", "vitriol", "vitriolic", "vituperate", "zeitgeist"};


        for (Character c = 'A'; c <= 'Z'; c++) groupMap.put(c, c.toString());

        for (Character c = 'a'; c <= 'z'; c++) groupMap.put(c, c.toString().trim().toUpperCase());


        dbRefManager.getWordDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot wordDatabaseSnap) {

                if (wordDatabaseSnap.exists()) {

                    // getting original database
                    for (DataSnapshot groupSnap : wordDatabaseSnap.getChildren()) {

                        originalWordMap.put(groupSnap.getKey(), new HashMap<String, Object>());
                        newWordDbMap.put(groupSnap.getKey(), new HashMap<String, Object>());


                        for (DataSnapshot wordSnap : groupSnap.getChildren()) {


                            WordModel2 wordModel = new WordModel2();

                            if (wordSnap.child("word").exists())
                                wordModel.setWord(wordSnap.child("word").getValue(String.class));
                            if (wordSnap.child("wordID").exists())
                                wordModel.setWordID(wordSnap.child("wordID").getValue(String.class));
                            if (wordSnap.child("image").exists())
                                wordModel.setImage(wordSnap.child("image").getValue(String.class));
                            if (wordSnap.child("note").exists())
                                wordModel.setNote(wordSnap.child("note").getValue(String.class));
                            if (wordSnap.child("type").exists())
                                wordModel.setType(wordSnap.child("type").getValue(String.class));


                            originalWordList.add(wordModel);
                            originalWordSet.add(wordModel.getWord().toLowerCase());
                            originalWordMap.get(groupSnap.getKey()).put(wordModel.getWordID(), wordModel);

                            if (oldWordRevisionMap.get(wordModel.getWordID()) != null) {
                                wordModel.setWordID(dbRefManager.getWordDatabaseReference().child(groupSnap.getKey()).push().getKey());
                            }

                            oldWordRevisionMap.put(wordModel.getWordID(), wordModel.getWord());
                            stringBuilder.append("\"");
                            stringBuilder.append(wordModel.getWord());
                            stringBuilder.append("\",");


                        }

                    }

                    Log.i("checkWithLog", String.valueOf(originalWordList.size()));

                    binding.currentWords.setText(stringBuilder.toString());

                    binding.currentWords.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            setClipboard(WordLoadActivity.this, stringBuilder.toString());

                        }
                    });

                    binding.proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog.show();
                            newWordsMap.clear();

                            //String newWordsEntered = binding.newWords.getText().toString();


                            for (String newWord : newWordsArr) {
                                newWord = newWord.trim();

                                if (!originalWordSet.contains(newWord)) {

                                    cat = groupMap.get(newWord.charAt(0));
                                    WordModel2 newWordModel = new WordModel2();

                                    newWordModel.setWordID(dbRefManager.getWordDatabaseReference().child(cat).push().getKey());
                                    newWordModel.setWord(capitalizeFirstCharacter(newWord));
                                    newWordModel.setType("Verb");


                                    originalWordMap.get(cat).put(newWordModel.getWordID(), newWordModel);
                                    newWordDbMap.get(cat).put(newWordModel.getWordID(), newWordModel);
                                    newWordsMap.put(newWordModel.getWordID(), newWordModel.getWord());
                                }


                            }

                            // set combined database
                            dbRefManager.getCombinedWordDatabaseReference().setValue(originalWordMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        // set new revision list
                                        dbRefManager.getRevisionDatabase().child("NEWWORDS").setValue(newWordsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    // set old revision list
                                                    dbRefManager.getRevisionDatabase().child("OLDWORDS").setValue(oldWordRevisionMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                // set new Database
                                                                dbRefManager.getNewWordDatabaseReference().setValue(newWordDbMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            binding.result.setText("successfull");

                                                                        } else {
                                                                            Log.i("checkWithLog", task.getResult().toString());
                                                                            binding.result.setText("4");

                                                                        }
                                                                        progressDialog.dismiss();
                                                                    }
                                                                });

                                                            } else {
                                                                Log.i("checkWithLog", task.getResult().toString());
                                                                binding.result.setText("3");
                                                                progressDialog.dismiss();

                                                            }

                                                        }
                                                    });

                                                } else {
                                                    Log.i("checkWithLog", task.getResult().toString());
                                                    binding.result.setText("2");
                                                    progressDialog.dismiss();

                                                }

                                            }
                                        });

                                    } else {
                                        Log.i("checkWithLog", task.getResult().toString());
                                        progressDialog.dismiss();
                                    }

                                }
                            });


                        }
                    });

                    progressDialog.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static String capitalizeFirstCharacter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char firstChar = Character.toUpperCase(input.charAt(0));
        return firstChar + input.substring(1);
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

}