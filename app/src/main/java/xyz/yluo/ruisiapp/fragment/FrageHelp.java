package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyHtmlTextView;

/**
 * Created by free2 on 16-7-14.
 */
public class FrageHelp extends Fragment {

    private static final String helpTxt =
            "本帮助参考 <a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&tid=824705&mobile=2\">【H∀】睿思帮助2.0　viaAH</a><br> 所有权归<a href=\"home.php?mod=space&uid=285519&do=profile&mobile=2\">@Adolf-Hitler</a>所有<br><hr>\n" +
                    "<strong>[站点信息]</strong><br>\n" +
                    "西电睿思是由西电学生自己创立的校内BT网站，于2009年9月13日上线，是目前西电唯一面向学生的高速免费BT下载站，同时睿思BBS也是最受西电师生欢迎的论坛之一，我们的口号是：爱西电、爱生活、爱睿思。<br>\n" +
                    "<br>\n" +
                    "<strong>[如何访问睿思]</strong><br>\n" +
                    "在浏览器内输入<a href=\"http://bbs.rs.xidian.me\" target=\"_blank\">http://bbs.rs.xidian.me</a>即可访问。本站仅支持西电校园网用户访问。<br>\n" +
                    "手机可以访问 http://bbs.rs.xidian.me 登陆BBS。<br>\n" +
                    "<br>\n" +
                    "<strong>[如何获得邀请码]</strong><br>\n" +
                    "你可以向同学或朋友索要；<br>\n" +
                    "一些公众平台也会发放邀请码；<br>\n" +
                    "还可以在BBS的<a href=\"http://bbs.rs.xidian.me/forum.php?mod=forumdisplay&amp;fid=137\" target=\"_blank\">邀请码申请专区</a>求助。<br>\n" +
                    "<br>\n" +
                    "<strong>[什么是BT/PT]</strong><br>\n" +
                    "BT:<br>\n" +
                    "比特流（BitTorrent）是一种内容分发协议，由布拉姆·科恩自主开发。它采用高效的软件分发系统和点对点技术共享大体积文件（如一部电影或电视节目），并使每个用户像网络重新分配结点那样提供上传服务。一般的下载服务器为每一个发出下载请求的用户提供下载服务，而BitTorrent的工作方式与之不同。分配器或文件的持有者将文件发送给其中一名用户，再由这名用户转发给其它用户，用户之间相互转发自己所拥有的文件部分，直到每个用户的下载都全部完成。这种方法可以使下载服务器同时处理多个大体积文件的下载请求，而无须占用大量带宽。<br>\n" +
                    "<br>\n" +
                    "事实上，睿思是一种特殊的BT，即PT：<br>\n" +
                    "PT（Private Tracker）是一种改良自BitTorrent协定的P2P下载方式，“Private Tracker”指私有种子服务器。与BT最大的不同点分别为可进行私密范围下载，及可统计每个用户的上载及下载量。<br>\n" +
                    "<br>\n" +
                    "建议新手可以读<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=806668\" target=\"_blank\"><font color=\"Red\"><strong>这篇文章</strong></font></a>，比较详细地介绍了PT的原理，有助于更好理解睿思的运作方式：<br>\n" +
                    "<br>\n" +
                    "<strong>[睿思BT部分和BBS部分的区分]</strong><br>\n" +
                    "BT部分主要是资源区，BBS则是论坛区。<br>\n" +
                    "<br>\n" +
                    "<strong>[现任管理员]</strong><br>\n" +
                    "你们自己填吧~<br>\n" +
                    "<strong>[论坛总则]</strong><br>\n" +
                    "<ul type=\"1\" class=\"litype_1\"><li>禁止使用攻击或挑衅他人的言辞；</li><li>禁止任何基于肤色、国家、民族、家族、宗教、性别、年龄、性取向及身体或精神方面的歧视性或攻击性言论；</li><li>请勿为了金币大肆灌水；</li><li>遵守论坛各版块版规，尊重管理员及论坛会员；</li><li>确保您的帖子适合发布于该版块；</li><li>避免双重发贴，如果你希望重新发布内容，请在你最后发布的内容选择编辑功能，而不是重新发布主题。<br>\n" +
                    "</li></ul><br>\n" +
                    "<strong>[资源帖评论总则]</strong><br>\n" +
                    "<ul type=\"1\" class=\"litype_1\"><li>无论如何，请尊重上传者；</li><li>伸手前请使用论坛“搜索”功能；</li><li>如果您没有下载的意向，请不要随便发表不负责任的、否定性的评论。<br>\n" +
                    "</li></ul><br>\n" +
                    "<strong>[资源上传总则]</strong><br>\n" +
                    "<ul type=\"1\" class=\"litype_1\"><li>发布种子前先行搜索是否发布，避免发布重复资源；</li><li>上传种子必须附加适当的描述。出种3人以上才能撤种，如无任何人下载，72 小时后可以撤种；</li><li>请勿恶意做假种，即利用BT软件强制做种并上传假流量（与原种内容其实完全不符）；</li><li>如果出现下列内容的种子，一律直接删除<br>\n" +
                    "</li></ul><div class=\"grey quote\"><blockquote>引用: 违背我国相关规定的任何资源； <br>\n" +
                    "美　国：NC-17级：（NO ONE 17 AND UNDER ADMITTED ）17岁或者以下不可观看；<br>\n" +
                    "英　国：“18\"级：适合18岁以上是成人观看；<br>\n" +
                    "香　港：第Ⅲ级：只准18岁（含）以上年龄的人观看；<br>\n" +
                    "新加坡：R(A)级为限制级（艺术）电影，只供21岁以上的成人观赏；<br>\n" +
                    "加拿大：“成人\"级(Adult)──只容许十八岁或以上人士观看。</blockquote></div>\n" +
                    "其它类资源规范同上，具体资源规范请参考各版块版规。<br>\n" +
                    "如果你拿不准自己要上传资源的类别，请将详细情况告知对应区版主或管理员，我们将站内信联系通知你可否发布。<br>\n" +
                    "<strong>[忘记用户名/密码如何找回]</strong><br>\n" +
                    "可以在登陆界面点击“找回密码”通过邮件找回，也可以去<a href=\"http://bbs.rs.xidian.me/forum.php?mod=forumdisplay&amp;fid=2\" target=\"_blank\">帮助区</a>发帖@管理员或去【睿思帮助QQ群：127528597】(验证信息：RS)寻求帮助。<br>\n" +
                    "<br>\n" +
                    "<strong>[BBS用户和权限说明]</strong><br>\n" +
                    "详见：<a href=\"http://bbs.rs.xidian.me/home.php?mod=spacecp&amp;ac=usergroup\" target=\"_blank\">用户组</a><br>\n" +
                    "<br>\n" +
                    "<strong>[如何邀请我的朋友加入]</strong><br>\n" +
                    "点击<a href=\"http://bbs.rs.xidian.me/home.php?mod=spacecp&amp;ac=invite\" target=\"_blank\">邀请好友</a>，使用论坛金币购买邀请码，将邀请码发送给朋友即可。<br>\n" +
                    "<br>\n" +
                    "<strong>[如何设置头像]</strong><br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=794744\" target=\"_blank\">怎样更换头像</a><br>\n" +
                    "<br>\n" +
                    "<strong>[在BBS上面发帖我该注意什么]</strong><br>\n" +
                    "<font color=\"Red\"><strong>遵守法规、校规和版规，每个版块的上面都有置顶的版规。</strong></font><br>\n" +
                    "<strong>[什么是种子]</strong><br>\n" +
                    "种子文件是记载下载文件的存放位置、大小、下载服务器的地址、发布者的地址等数据的一个索引文件。<br>\n" +
                    "如果是用BT下载电影，那必须有这个电影文件的种子，这个种子文件并不是要下载的电影，但是要下载这个电影则必须先下载他的种子文件。<br>\n" +
                    "种子文件的后缀名是.torrent，用相应的软件打开种子文件，才可以下载你所需要的资源。<br>\n" +
                    "<br>\n" +
                    "<strong>[我该如何找到自己喜欢的资源]</strong><br>\n" +
                    "点击论坛上方的种子列表，输入自己想要搜索的内容即可。其中有标记的种子不计下载数据量。<br>\n" +
                    "<br>\n" +
                    "<strong>[我该如何下载种子，使用何种软件打开种子文件]</strong><br>\n" +
                    "点击种子的下载图标，或者链接，即可下载种子。<br>\n" +
                    "如果弹出迅雷，请在迅雷设置中将“监视设置”—“监视浏览器”的选项取消，并在“BT设置”—“关联设定”中取消关联BT种子文件（.torrent文件）。<br>\n" +
                    "<br>\n" +
                    "请注意<font color=\"Red\"><strong>不可以</strong></font>在西电睿思BT使用的软件包括：<br>\n" +
                    "<table><tbody><tr><td> 比特彗星(BitComet)</td><td> 比特精灵</td><td> QQ超级旋风</td><td> 迅雷</td><td> 快车(FlashGet)</td></tr><tr><td> Opera</td><td> DNA</td><td> MLDonkey</td><td> uTorrent的<br>\n" +
                    "较低版本</td><td> uTorrent 1.80</td></tr><tr><td> uTorrent 1.81</td><td> uTorrent 1.82</td><td> uTorrent 1.83 Beta</td><td> uTorrent 2.0 Beta</td><td> Azureus for windows</td></tr></tbody></table><br>\n" +
                    "可以使用的软件如下：<br>\n" +
                    "<table><tbody><tr><td>uTorrent 1.83</td><td>uTorrent 1.84</td><td>uTorrent 1.90Beta </td><td>uTorrent 2.x</td><td>uTorrent 3.x</td></tr><tr><td>Azureus</td><td>Transmission</td><td>rtorrent</td><td>Deluge</td><td>KTorrent</td></tr></tbody></table><br>\n" +
                    "<br>\n" +
                    "推荐使用uTorrent进行上传下载。<br>\n" +
                    "点击<a href=\"http://bbs.rs.xidian.me/uTorrent.zip\" target=\"_blank\">此处下载</a>（绿色版）。<br>\n" +
                    "<br>\n" +
                    "<strong>[Linux 系统及Mac系统使用特殊说明]</strong><br>\n" +
                    "由于目前uTorrent并未对Linux系统进行很好的支持，所以Linux用户请使用Deluge或者Transmission；<br>\n" +
                    "Mac 系统用户可以使用Transmission或者uTorrent for Mac。<br>\n" +
                    "<br>\n" +
                    "<strong>[为什么要保持上传，如何上传]</strong><br>\n" +
                    "BT的重要精神就是<strong><font color=\"Red\"><font style=\"background-color:Yellow\">分享</font></font></strong>，你所下载的资源都是别人为你上传的，所以你也需要为别人提供上传，这不仅是为了保证资源更好的延续下去，同时也是对发种人的尊重，如果<font color=\"Red\"><strong>分享率过低会失去下载权限</strong></font>。<br>\n" +
                    "保持UT开启，同时，不要移动或者删除下载的文件，即可保持上传状态，当别人从你这里获取资源时，你就提供了上传数据量。<br>\n" +
                    "<br>\n" +
                    "<strong>[什么是分享率]</strong><br>\n" +
                    "简单来说，就是上传量和下载量的比值，此举是为了更好地分享资源，增加保种率。<br>\n" +
                    "<br>\n" +
                    "<strong>[BT用户组级别说明]</strong><br>\n" +
                    "0-20G下载量 共享率不受限制<br>\n" +
                    "20-100G下载量  共享率要≥ X/100<br>\n" +
                    ">100G以上下载量 共享率要>1\n" +
                    "受限用户可以通过做种、<font color=\"Red\"><strong>下载免费种子并保持上传</strong></font>的方式增加共享率。<br>\n" +
                    "<br>\n" +
                    "<strong>[如果我有好的资源，我应该如何发布]</strong><br>\n" +
                    "首先，你应该满足：<br>\n" +
                    "A注册满三天<br>\n" +
                    "B.用户组达到幼儿园<br>\n" +
                    "C.上传流量达到10G<br>\n" +
                    "D.如果您已满足以上三条，请确认自己账号的健康度，是否受限，注意受限用户是没有发种权限的。<br>\n" +
                    "其次，了解自己所发资源所属类型，并阅读相应版规，确认非重复资源，非违禁资源后即可发布；<br>\n" +
                    "再次，阅读发种教程，请不要遗漏任何一步。 <br>\n" +
                    "<br>\n" +
                    "推荐发种教程：<br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=416397\" target=\"_blank\">发种教程1</a> <br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=377353\" target=\"_blank\">发种教程2</a> <br>\n" +
                    "<br>\n" +
                    "<strong>[我的系统重装了，我该如何继续上传]</strong><br>\n" +
                    "重新下载种子，并选择文件所在路径，跳过散列检测之后，确定即可继续上传。<br>\n" +
                    "<br>\n" +
                    "<strong>[UT的种子变红了，我该怎么办]</strong><br>\n" +
                    "如果uTorrent红种（即种子状态为红颜色），请参见:<br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;&amp;tid=219048\" target=\"_blank\">根据tracker状态排除红种</a><br>\n" +
                    "一般是服务器负载过大，等待即可。 <br>\n" +
                    "如果tracker状态显示离线，请将DNS设置为自动获取，然后重启。<br>\n" +
                    "<br>\n" +
                    "<strong>[我该如何设置uTorrent]</strong><br>\n" +
                    "一些资料：<br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=160593\" target=\"_blank\">http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=160593</a> <br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=380778\" target=\"_blank\">http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=380778</a> <br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=403213\" target=\"_blank\">http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=403213</a><br>\n" +
                    "<br>\n" +
                    "<strong>[最常见的数据未能得到更新的原因]</strong><br>\n" +
                    "服务器过载/未响应。只要尝试着保持会话直到服务器恢复响应(不推荐连续手动刷新，这样会加重服务器负载)。<br>\n" +
                    "你正在使用不稳定的客户端。如果你想要使用测试版或者CVS版本，你需要自己承担由此带来的风险。<br>\n" +
                    "<br>\n" +
                    "<strong>[为什么置顶的种子总在变动]</strong><br>\n" +
                    "为了不影响用户查看种子列表，置顶种子只显示最新的10个，可以通过分类搜索查看所有置顶资源。<br>\n" +
                    "<br>\n" +
                    "<strong>[为何我下载的资源没有速度]</strong><br>\n" +
                    "请查看你的tracker状态。可能是服务器负载太大，没有响应，一般过一段时间就会好。<br>\n" +
                    "也有可能是你的共享率太低，已成为受限用户无法下载。<br>\n" +
                    "还有可能是种子刚发布或发布时间太久远，做种人较少，多等等即可。<br>\n" +
                    "如果没有做种者，请站内信发种人或者下载过该资源的人询问。<br>\n" +
                    "假期做种人数少，常见无种的情况，请大家谅解。<br>\n" +
                    "<br>\n" +
                    "另外，由于转种及防作弊系统升级，2012年6月16日至2012年10月23日所发的种子均已被系统更新。若出现种子没有速度的情况，请使用紫色下载图标下载种子。做种用户，请下载蓝色新种子更新资源，以免造成死种。由系统升级所带来的不便，敬请各位用户谅解。<br>\n" +
                    "<br>\n" +
                    "<strong>[我的BT权限受限了怎么办--多长时间可以恢复]</strong><br>\n" +
                    "可以通过下载有标记的种子的方式，加大上传逐渐提高共享率。睿思每天更新两次数据库，如果没有及时更新，请不要着急。<br>\n" +
                    "<strong>[我该如何回帖/发帖]</strong><br>\n" +
                    "请阅读相应版块的版规，不要进行人身攻击，脏话，请文明用语。<br>\n" +
                    "发帖请点击论坛发帖按钮，注意<strong><font color=\"Red\">选择准确的版块</font></strong>以便用户能更好的区分。<br>\n" +
                    "<br>\n" +
                    "<strong>[为什么我会被删帖、删种]</strong><br>\n" +
                    "如果你的帖子或种子被删除或无权查看，请阅读你发表内容版块的版规，如有异议请及时站内信联系版主或管理员。<br>\n" +
                    "<br>\n" +
                    "<strong>[BBS的用户组是什么意思]</strong><br>\n" +
                    "BBS的用户组会区分不同的权限，比如附小级别以上才可以使用论坛签名功能等。<br>\n" +
                    "更多级别权限请看：<br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/home.php?mod=spacecp&amp;ac=usergroup\" target=\"_blank\">用户组</a><br>\n" +
                    "<br>\n" +
                    "<strong>[如何添加视频、图片、链接]</strong><br>\n" +
                    "回复或发帖时点击高级模式即可看到。<br>\n" +
                    "如果是发布图片，请不要使用外链。<br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=765708\" target=\"_blank\">发图</a><br>\n" +
                    "<br>\n" +
                    "<strong>[如何增加我的金币和人品]</strong><br>\n" +
                    "发帖可以得到金币，优秀的帖子和回复以及参加一些论坛活动也会获得金币和人品。<br>\n" +
                    "同时一些级别的用户组用户、版主和管理员可以给他人评分(增减金币或人品)。<br>\n" +
                    "<br>\n" +
                    "<strong>[我该如何签到]</strong><br>\n" +
                    "点击右上角的插件，选择<a href=\"http://bbs.rs.xidian.me/plugin.php?id=dsu_paulsign:sign\" target=\"_blank\">每日签到</a>即可。<br>\n" +
                    "<br>\n" +
                    "<strong>[如何查看今日水神发帖数]</strong><br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/misc.php?mod=ranklist&amp;type=member&amp;view=post&amp;orderby=today\" target=\"_blank\">今日水神榜</a><br>\n" +
                    "<br>\n" +
                    "<strong>[如何自定义头衔]</strong><br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/home.php?mod=spacecp&amp;ac=profile&amp;op=info\" target=\"_blank\">个人资料</a><br>\n" +
                    "<br>\n" +
                    "<strong>[如何赠送金币]</strong><br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/home.php?mod=spacecp&amp;ac=credit&amp;op=transfer\" target=\"_blank\">转账</a><br>\n" +
                    "<br>\n" +
                    "<strong>[如何设置签名]</strong><br>\n" +
                    "用户组到达：西电托儿所<br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=viewthread&amp;tid=683060\" target=\"_blank\">个性化签名</a><br>\n" +
                    "<br>\n" +
                    "<strong>[如何删除自己的帖子]</strong><br>\n" +
                    "悔悟卡删除后，只有联系对应区版主或管理员才能删帖。<br>\n" +
                    "交易区若非违规不能删帖。<br>\n" +
                    "<strong>[声明]</strong><br>\n" +
                    "本站为校园内部<font color=\"Red\"><strong>非盈利性</strong></font>公益站点，所有资源来源于网友搜集，本站自身不存储、编辑或修改第三方资源。<br>\n" +
                    "本站资源仅供个人学习、研究使用，严禁用于任何商业用途。<br>\n" +
                    "本站严禁传播任何违法、违规以及含有任何反动、色情、暴力的资源和信息，一经发现，本站将严肃处理，情节严重者将移交相关部门处理。<br>\n" +
                    "若您发现本站的资源和信息涉及版权问题，请与我们联系，我们会立即处理。<br>\n" +
                    "<br>\n" +
                    "帮助文档可能滞后于BBS和BT的修改和更新，睿思保留在不更新帮助文档的前提下更新或修改BBS和BT的权利。<br>\n" +
                    "睿思保留随时更新本帮助文档的权利，恕不另行通知。<br>\n" +
                    "以任何方式访问或使用本网站，即表示您同意网站的规则和条款并受其约束。如果您不同意网站的规则和条款，请不要访问或使用本网站。<br>\n" +
                    "本帮助文档最终解释权归睿思所有。<br>\n" +
                    "<br>\n" +
                    "新浪微博：http://weibo.com/xdrsbt <br>\n" +
                    "人人主页：http://page.renren.com/60103244<br>" +
                    "<strong>[关于此帮助]</strong><br>\n" +
                    "本帮助文档由<a href=\"http://bbs.rs.xidian.me/home.php?mod=space&amp;uid=285519\" target=\"_blank\"><font color=\"Blue\"><strong>@Adolf-Hitler</strong></font></a>更新。<br>\n" +
                    "对于帮助文档的建议和意见请在本帖内回复。<br>\n" +
                    "<br>\n" +
                    "<a href=\"http://bbs.rs.xidian.me/forum.php?mod=misc&amp;action=attachpay&amp;aid=832572&amp;tid=798802\" target=\"_blank\"><font size=\"6\"><strong><font color=\"Cyan\"><font style=\"background-color:Purple\">睿思帮助文档下载</font></font></strong></font></a><br>\n" +
                    "<br>\n" +
                    "<strong>[系统更新信息]</strong><br>\n" +
                    "2012-12-07 默默上线 <br>\n" +
                    "2014-07-14 论坛升级为X3.2<br>";

    public FrageHelp() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frage_help, container, false);
        MyHtmlTextView htmlTextView = (MyHtmlTextView) view.findViewById(R.id.html_text);
        htmlTextView.mySetText(getActivity(), helpTxt);

        return view;
    }
}
