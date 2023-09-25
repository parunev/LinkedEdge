package com.parunev.linkededge.util.email;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailPatterns {

    public static String confirmationEmail(String name, String link) {
        return "<div style=\"font-family: Helvetica, Arial, sans-serif; font-size: 16px; margin: 0; color: #b0c0c\">\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse: collapse; min-width: 100%; width: 100% !important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td width=\"100%\" height=\"53\" bgcolor=\"3399FF\">\n" +
                "          <table role=\"presentation\" width=\"100%\" style=\"border-collapse: collapse; max-width: 580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "            <tbody>\n" +
                "              <tr>\n" +
                "                <td width=\"70\" bgcolor=\"#3399FF\" valign=\"middle\">\n" +
                "                  <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse\">\n" +
                "                    <tbody>\n" +
                "                      <tr>\n" +
                "                        <td style=\"padding-left: 10px\"></td>\n" +
                "                        <td style=\"font-size: 28px; line-height: 1.315789474; Margin-top: 4px; padding-left: 10px\">\n" +
                "                          <span style=\"font-family: Arial, Arial, sans-serif; font-weight: 100; color: #FFFF; text-decoration: none; vertical-align: top; display: inline-block\">Welcome to LinkedEdge!</span>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </tbody>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </tbody>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                " <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse; max-width: 580px; width: 100% !important\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "        <td>\n" +
                "          <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse\">\n" +
                "            <tbody>\n" +
                "              <tr>\n" +
                "                <td bgcolor=\"#0370B8\" width=\"100%\" height=\"10\"></td>\n" +
                "              </tr>\n" +
                "            </tbody>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse; max-width: 580px; width: 100% !important\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "        <td style=\"font-family: Helvetica, Arial, sans-serif; font-size: 19px; line-height: 1.315789474; max-width: 560px\">\n" +
                "          <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">Dear <b>" + name + "</b>,</p>\n" +
                "          <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">Thank you for joining <b>LinkedEdge</b>, your AI Interview assistant for generating interview questions via LinkedIn. We're thrilled to have you as part of our community.</p>\n" +
                "          <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">To get started with and enhance your application experience, please verify your email address:</p>\n" +
                "          <blockquote style=\"Margin: 0 0 20px 0; border-left: 10px solid #3399FF; padding: 15px 0 0.1px 15px; font-size: 19px; line-height: 25px\">\n" +
                "            <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #1b1c1c\">\n" +
                "              <a href=\"" + link + "\">Verify Email</a>\n" +
                "            </p>\n" +
                "          </blockquote>\n" +
                "          <p style=\"color: #000;\">The verification link will expire in <b>24 hours</b>.</p>\n" +
                "          <p style=\"color: #000;\">Best regards,<br>\n" +
                "            <b>The LinkedEdge Team</b></p>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "  <div class=\"yj6qo\"></div>\n" +
                "  <div class=\"adL\">\n" +
                "  </div>\n" +
                "</div>\n";
    }
}
