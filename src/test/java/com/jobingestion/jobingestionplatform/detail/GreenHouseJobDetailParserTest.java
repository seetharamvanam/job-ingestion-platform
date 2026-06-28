package com.jobingestion.jobingestionplatform.detail;


import com.jobingestion.jobingestionplatform.provider.greenhouse.detail.GreenhouseJobDetailParser;
import com.jobingestion.jobingestionplatform.provider.greenhouse.detail.JobDetail;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GreenHouseJobDetailParserTest {


    private GreenhouseJobDetailParser greenhouseJobDetailParser;
    private final String html = "<div class=\"job__description body\"><div><p><img style=\"display: none; max-width: 100%;\" src=\"./Job Application for Representative, Voice Ordering Accessibility at DoorDash USA_files/a31.png\" width=\"1px\"> <img style=\"display: none; max-width: 100%;\" src=\"./Job Application for Representative, Voice Ordering Accessibility at DoorDash USA_files/i.gif\" alt=\"\" width=\"1\"></p></div><div><h2><strong>About the Team</strong></h2>\n" +
            "<p>We're looking to grow our Voice Ordering Live Operations team by leaps &amp; bounds. Join DoorDash as a Voice Ordering Accessibility Representative to become an expert in supporting the needs of DoorDash's thousands of partner Merchants and customers. You'll be part of an exciting team designed to take ordering food to the next level. Representatives will report to the Voice Ordering Team Supervisor.</p>\n" +
            "<h2><strong>About the Role</strong></h2>\n" +
            "<p>Note: We are a 24 hour/365 day operation. Due to this need, employees must have flexibility to work varying schedules. An employee will be assigned a schedule that may include evenings, overnight, weekends, and holidays. These schedules will be adjusted periodically to meet the needs of our business.</p>\n" +
            "<p>Due to PCI/DSS compliance, this role requires you to work full time from our Tempe, Arizona office and has strict policies on cell phone usage.</p>\n" +
            "<p>This position is fully in-office for the first 90 days. After successful completion of that period, a hybrid schedule may be offered based on business needs.&nbsp;</p>\n" +
            "<h2><strong>You’re excited about this opportunity because you will…</strong></h2>\n" +
            "<ul>\n" +
            "<li>Support inbound live order calls for DoorDash's partner Merchants and customers.</li>\n" +
            "<li>Develop expertise in how DoorDash uses its processes &amp; system to promote positive outcomes.</li>\n" +
            "<li>Operate with a willingness to grow with a diverse team that has a passion for what we do &amp; the impact we are making toward this industry.</li>\n" +
            "<li>Go above &amp; beyond to create the best ordering experience for our accessibility needs customers, including but not limited to our visually impaired customers.</li>\n" +
            "<li>Assist our accessibility needs customers with accurate order placement and local restaurant recommendations.&nbsp;</li>\n" +
            "<li>Securely update important account information for our accessibility needs customers, including emails and credit card information.</li>\n" +
            "<li>Provide feedback and share your ideas on how DoorDash can better the ordering customer experience.</li>\n" +
            "</ul>\n" +
            "<h2><strong>We’re excited about you because…</strong></h2>\n" +
            "<ul>\n" +
            "<li>1-2 years of customer service experience via phone, email, or chat, open to customer-facing experience as well.</li>\n" +
            "<li>You have strong written &amp; verbal communication skills including top-notch grammar, spelling &amp; punctuation.</li>\n" +
            "<li>You are customer-obsessed &amp; love helping others.</li>\n" +
            "<li>You work well in a team environment, contributing to a collaborative work environment where people learn from one another and continuously improve processes on behalf of users</li>\n" +
            "<li>You remain calm under pressure! You troubleshoot problems and find speedy resolutions in high-pressure, time-sensitive situations and can determine when to escalate, and when to resolve.</li>\n" +
            "<li>You have experience offering customers products or services that best meet their needs.</li>\n" +
            "<li>You are within a commutable distance to our Tempe, AZ office.</li>\n" +
            "<li>You are comfortable working in a PCI/DSS environment</li>\n" +
            "</ul>\n" +
            "<p>&nbsp;</p>\n" +
            "<p>&nbsp;</p>\n" +
            "<p style=\"font-weight: 500;\">Notice to Applicants for Jobs Located in NYC or Remote Jobs Associated With Office in NYC Only</p>\n" +
            "<p>We use Covey as part of our hiring and/or promotional process for jobs in NYC and certain features may qualify it as an AEDT in NYC. As part of the hiring and/or promotion process, we provide Covey with job requirements and candidate submitted applications. We began using <a href=\"https://getcovey.com/product/covey-scout-inbound\">Covey Scout for Inbound</a> from August 21, 2023, through December 21, 2023, and resumed using <a href=\"https://getcovey.com/product/covey-scout-inbound\">Covey Scout for Inbound</a> again on June 29, 2024.</p>\n" +
            "<p>The Covey tool has been reviewed by an independent auditor. Results of the audit may be viewed here: <a style=\"text-decoration: underline;\" href=\"https://getcovey.com/nyc-local-law-144\">Covey</a></p></div><div><h2>About DoorDash</h2>\n" +
            "<p>At DoorDash, our mission to empower local economies shapes how our team members move quickly, learn, and reiterate in order to make impactful decisions that display empathy for our range of users—from Dashers to merchant partners to consumers. We are a technology and logistics company that started by enabling door-to-door delivery, and we are looking for team members who can help us go from a company that is known as the place you order food to a company that people turn to for any and all goods.<br><br>DoorDash is growing rapidly and changing constantly, which gives our team members the opportunity to share their unique perspectives, solve new challenges, and own their careers. We're committed to supporting employees’ happiness, healthiness, and overall well-being by providing comprehensive benefits and perks including premium healthcare, wellness expense reimbursement, paid parental leave and more.</p>\n" +
            "<h2>Our Commitment to Diversity and Inclusion</h2>\n" +
            "<p>We’re committed to growing and empowering a more inclusive community within our company, industry, and cities. That’s why we hire and cultivate diverse teams of people from all backgrounds, experiences, and perspectives. We believe that true innovation happens when everyone has room at the table and the tools, resources, and opportunity to excel.</p>\n" +
            "<p>Statement of Non-Discrimination: In keeping with our beliefs and goals, no employee or applicant will face discrimination or harassment based on: race, color, ancestry, national origin, religion, age, gender, marital/domestic partner status, sexual orientation, gender identity or expression, disability status, or veteran status. Above and beyond discrimination and harassment based on “protected categories,” we also strive to prevent other subtler forms of inappropriate behavior (i.e., stereotyping) from ever gaining a foothold in our office. Whether blatant or hidden, barriers to success have no place at DoorDash. We value a diverse workforce – people who identify as women, non-binary or gender non-conforming, LGBTQIA+, American Indian or Native Alaskan, Black or African American, Hispanic or Latinx, Native Hawaiian or Other Pacific Islander, differently-abled, caretakers and parents, and veterans are strongly encouraged to apply. Thank you to the Level Playing Field Institute for this statement of non-discrimination.</p>\n" +
            "<p>Pursuant to the San Francisco Fair Chance Ordinance, Los Angeles Fair Chance Initiative for Hiring Ordinance, and any other state or local hiring regulations, we will consider for employment any qualified applicant, including those with arrest and conviction records, in a manner consistent with the applicable regulation.</p>\n" +
            "<p><strong>If you need any accommodations, please inform your recruiting contact upon initial connection.<br><br></strong></p>\n" +
            "<p>Notice to Applicants for Jobs Located in NYC or Remote Jobs Associated With Office in NYC Only</p>\n" +
            "<p>We used Covey as part of our hiring and/or promotional process for jobs in NYC and certain features may qualify it as an AEDT in NYC. As part of the hiring and/or promotion process, we provided Covey with job requirements and candidate submitted applications. We began using Covey Scout for Inbound from August 21, 2023, through December 21, 2023.&nbsp; We resumed using Covey Scout for Inbound again on June 29, 2024, and ceased using Covey Scout for Inbound on April 30, 2026.</p>\n" +
            "<p>The Covey tool has been reviewed by an independent auditor. Results of the audit may be viewed here:<strong>&nbsp;<a href=\"https://getcovey.com/nyc-local-law-144\">https://getcovey.com/nyc-local-law-144</a>.<br></strong></p></div></div>";

    @BeforeEach
    void setUp() {
        greenhouseJobDetailParser = new GreenhouseJobDetailParser();
    }

    @Test
    void shouldExtractJobDescriptionWhenJobDescriptionExists(){
        Document document = Jsoup.parse(html);
        String jobDetail = greenhouseJobDetailParser.parse(document);
        assertTrue(jobDetail.contains("About the Team"));
        assertTrue(jobDetail.contains("1-2 years"));
    }

    @Test
    void shouldReturnEmptyDescriptionWhenJobDescriptionDoesNotExist(){
        String HTML = "<html>\n" +
                "  <body>\n" +
                "    <h1>Test Job</h1>\n" +
                "  </body>\n" +
                "</html>";
        Document document = Jsoup.parse(HTML);
        String jobDetail = greenhouseJobDetailParser.parse(document);
        assertFalse(jobDetail.equals("Unknown"));
        assertTrue(jobDetail.isEmpty());
    }
}
